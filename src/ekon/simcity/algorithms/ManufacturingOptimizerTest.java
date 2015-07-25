package ekon.simcity.algorithms;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

import ekon.simcity.city.Factory;
import ekon.simcity.city.FactoryType;
import ekon.simcity.city.Item;
import ekon.simcity.city.ManufacturingFacility;
import ekon.simcity.city.ManufacturingFacility.Slot;
import ekon.simcity.city.ProductionRunner;
import ekon.simcity.city.Store;

public class ManufacturingOptimizerTest {
  
  private ProductionRunner prodRunner;
  
  @Before
  public void setUp() {
	prodRunner = new ProductionRunner();
  }
  
  private void setUpFactory(List<Item> unlockedItems, Item... itemsInProd) {
	ManufacturingFacility factory = new Factory(FactoryType.SMALL_FACTORY);
	prodRunner.addFacility(factory);
	setUpManufacturingFacility(factory, unlockedItems, itemsInProd);
  }
  
  private void setUpStore(ManufacturingFacility.Type type, int capacity, List<Item> unlockedItems, Item... itemsInProd) {
	ManufacturingFacility store = new Store(type, capacity);
	prodRunner.addFacility(store);
	setUpManufacturingFacility(store, unlockedItems, itemsInProd);
  }

  private void setUpManufacturingFacility(ManufacturingFacility facility, List<Item> unlockedItems, Item... itemsInProd) {
	for (Item item : unlockedItems) {
	  prodRunner.unlockItem(item);
	}

	for (Item item : itemsInProd) {
	  prodRunner.getFacility(facility.getType()).makeItems(item);
	}
  }
  
  private ManufacturingFacility getFactory() {
	return prodRunner.getFacility(ManufacturingFacility.Type.FACTORY);
  }
  
  /** Verify that given just a single factory, the most expensive item can be manufactured */
  @Test
  public void testOptimizeToSellMostExpensiveItems_basic() {
	setUpFactory(ImmutableList.of(Item.METAL));
	
	ManufacturingOptimizer optimizer = new ManufacturingOptimizer();
	optimizer.optimizeToSellMostExpensiveItems(false /* useExistingItems */, prodRunner);

	ManufacturingFacility facility = getFactory();
	assertEquals(0, facility.getNumFreeSlots()); // all slots should be taken up with metal
	for (Slot slot : facility.getSlots()) {
	  assertEquals(Item.METAL, slot.getItem());
	}
  }

  /**
   * Verify that we don't try to manufacture the most expensive item again if
   * it's already in production and we can use it.
   * 
   * TODO: technically we might want to be able to do this!!
   */
  @Test
  public void testOptimizeToSellMostExpensiveItems_alreadyInProduction_useExistingItemsAllowed() {
	setUpFactory(ImmutableList.of(Item.METAL), Item.METAL);
	
	// Verify setup.
	ManufacturingFacility facility = getFactory();
	assertEquals(facility.getSlots().size() - 1, facility.getNumFreeSlots()); // one slot taken to make a metal, as before
	assertEquals(Item.METAL, facility.getSlots().get(0).getItem());
	
	ManufacturingOptimizer optimizer = new ManufacturingOptimizer();
	optimizer.optimizeToSellMostExpensiveItems(true /* useExistingItems */, prodRunner);

	// TODO: fix test!!!! there is not way to verify that we're not reusing existing items. This needs to be more complicated, liek a recursive test.
	assertEquals(0, facility.getNumFreeSlots()); // all slots should be taken up with metal
	for (Slot slot : facility.getSlots()) {
	  assertEquals(Item.METAL, slot.getItem());
	}
  }
  
  /**
   * Verify that we try to manufacture the most expensive item again if
   * it's already in production and we cannot use it.
   * 
   * TODO: technically we might want to be able to do this!!
   */
  @Test
  public void testOptimizeToSellMostExpensiveItems_alreadyInProduction_useExistingItemsNotAllowed() {
	setUpFactory(ImmutableList.of(Item.METAL), Item.METAL);
	
	// Verify setup.
	ManufacturingFacility facility = getFactory();
	assertEquals(facility.getSlots().size() - 1, facility.getNumFreeSlots()); // one slot taken to make a metal, as before
	assertEquals(Item.METAL, facility.getSlots().get(0).getItem());
	
	ManufacturingOptimizer optimizer = new ManufacturingOptimizer();
	optimizer.optimizeToSellMostExpensiveItems(false /* useExistingItems */, prodRunner);
	
	// TODO: fix test!!!! there is not way to verify that we're not reusing existing items. This needs to be more complicated, liek a recursive test.
	assertEquals(0, facility.getNumFreeSlots()); // all slots should be taken up with metal
	for (Slot slot : facility.getSlots()) {
	  assertEquals(Item.METAL, slot.getItem());
	}
  }

  /** Verify that we do actually try to manufacture the most expensive item available. */
  @Test
  public void testOptimizeToSellMostExpensiveItems_manufacturingMostExpensive() {
	setUpFactory(ImmutableList.of(Item.METAL, Item.WOOD));

	ManufacturingOptimizer optimizer = new ManufacturingOptimizer();
	optimizer.optimizeToSellMostExpensiveItems(false /* useExistingItems */, prodRunner);

	ManufacturingFacility facility = getFactory();
	assertEquals(0, facility.getNumFreeSlots()); // all slots should be taken up with metal
	for (Slot slot : facility.getSlots()) {
	  assertEquals(Item.WOOD, slot.getItem());
	}
  }
  
  /** Verify that we manufacture required ingredient as well if they're not available in production. */
  @Test
  public void testOptimizeToSellMostExpensiveItems_manufacturingRecursively() {
	setUpFactory(ImmutableList.of(Item.METAL));
	setUpStore(ManufacturingFacility.Type.BUILDING_SUPPLY_STORE, 2, ImmutableList.of(Item.NAILS));

	ManufacturingOptimizer optimizer = new ManufacturingOptimizer();
	optimizer.optimizeToSellMostExpensiveItems(false /* useExistingItems */, prodRunner);

	// Verify that 1 nail and 2 metal is in production.
	// TODO: technically this isn't quite right because the metals aren't ready yet to put the nail in production.

	ManufacturingFacility facility = getFactory();
	assertEquals(0, facility.getNumFreeSlots()); // all slots should be taken up with metal
	for (Slot slot : facility.getSlots()) {
	  assertEquals(Item.METAL, slot.getItem());
	}

	facility = prodRunner.getFacility(ManufacturingFacility.Type.BUILDING_SUPPLY_STORE);
	assertEquals(facility.getSlots().size() - 1, facility.getNumFreeSlots()); // one slot taken to make a nail, there should not be enough metal to make a second nail
	assertEquals(Item.NAILS, facility.getSlots().get(0).getItem());
  }
}
