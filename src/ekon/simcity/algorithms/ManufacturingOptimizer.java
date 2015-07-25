package ekon.simcity.algorithms;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import ekon.simcity.city.Item;
import ekon.simcity.city.Item.MaxSellingPriceComparator;
import ekon.simcity.city.ManufacturingFacility;
import ekon.simcity.city.ManufacturingFacility.Slot;
import ekon.simcity.city.ProductionRunner;

/**
 * Optimizer running factories and stores to their full potential.
 * 
 * This makes tradeoffs for making items for:
 * - building buildings
 * - recovering from Dr. Vu's disasters
 * - selling
 * - cargo ship fulfillments
 * 
 * Note: This is not thread-safe since stores and factories are not immutable.
 */
class ManufacturingOptimizer {


  /**
   * Optimize the manufacturing facilities for selling items on the exchange.
   * 
   * Given an initial snapshot of the manufacturing facilities, it modifies it using this algorithm.
   */
  void optimizeForSelling(ProductionRunner productionRunner) {
	
	// A simple starting strategy is to try to sell the most expensive items from each facility.
  }
  
  /**
   * Optimizes the manufacturing facilities to sell the highest-priced items.
   * This could be useful for setting up at night.
   *  
   *  @param useExistingItems true if allowed to use items in the facility for making chosen items
   *  @param productionRunner takes in a snapshot  of the manufacturing facilities that it modifies using its algorithm
   */
  void optimizeToSellMostExpensiveItems(boolean useExistingItems, ProductionRunner productionRunner) {
	// Sort items by price and go through them to fill up manufacturing slots until there are none left.

	// TOOD: not sure if i can just pass an array to a vararg constructor for asList.
	List<Item> itemsSortedByMaxSellingPrice = Lists.newArrayList(productionRunner.getUnlockedItems().values());
	itemsSortedByMaxSellingPrice.sort(new MaxSellingPriceComparator()); // looks like this is in increasing order, but I want it in decreasing order.
	
	// Dump existing items in production into a temp map which we can then use
	// for reserving existing items, if that's an option.
	Map<Item, Integer> itemsInProduction = new HashMap<>();
	if (useExistingItems) {
	  for (ManufacturingFacility facility : productionRunner.getFacilities().values()) {
		for (Slot slot : facility.getSlots()) {
		  if (slot.hasItem()) {
			Item item = slot.getItem();
			itemsInProduction.put(item, itemsInProduction.containsKey(item) ? itemsInProduction.get(item) + 1 : 1);
		  }
		}
	  }
	}

	// TODO: Want to be able to manufacture as many of the most expensive type of product as possible, then move on to the less expensive ones.
	
	int expensiveItemIndex = itemsSortedByMaxSellingPrice.size() - 1;
	Item expensiveItem = itemsSortedByMaxSellingPrice.get(expensiveItemIndex);
	do {
	  // Recursively find required ingredients and add to facilities if not
	  // already there.
	  // Create a plan for the required ingredients for the item. Then verify if
	  // all required ingredients
	  // will fit in the open slots. Otherwise, don't make this item.

	  // This is to keep track of which existing items in facilities we're going
	  // to make use of.
	  // This will be needed if we choose to not make an item so that we can add
	  // its required ingredients back into the
	  // itemsInProduction map.
	  Map<Item, Integer> itemsSubtractedFromProduction = new HashMap<>();

	  // Keeps track of the number of slots required in each facility for manufacturing item.
	  // This is used for determining if we have enough required slots in each facility to produce item and its ingredients.
	  Map<ManufacturingFacility.Type, Integer> facilityTypeToNumSlotsMap = new HashMap<>();

	  // Keeps track of all of the items that need to be manufactured. This is related to facilityTypeToNumSlotsMap
	  // in that facilityTypeToNumSlotsMap keeps track of the number of slots needed in each facility instead of the items.
	  // Seems easier to keep track of these things separately instead of in a single data structure for easier access.
	  Map<Item, Integer> allRequiredItems = new HashMap<>();
	  tryToManufactureItem(expensiveItem, 1 /* numCopeis */, itemsInProduction, itemsSubtractedFromProduction,
		  facilityTypeToNumSlotsMap, allRequiredItems);

	  // Verify if we can actually manufacture this item.
	  boolean canManufactureItem = true;
	  for (ManufacturingFacility.Type manufacturingFacilityType : facilityTypeToNumSlotsMap.keySet()) {
		if (!productionRunner.hasFacility(manufacturingFacilityType)) {
		  canManufactureItem = false;
		  break;
		}

		int neededSlots = facilityTypeToNumSlotsMap.get(manufacturingFacilityType);
		int freeSlots = productionRunner.getFacility(manufacturingFacilityType).getNumFreeSlots();
		if (freeSlots < neededSlots) {
		  canManufactureItem = false;
		  break;
		}
	  }
	  
	  if (canManufactureItem) {
		// Now that we know we can manufacture, actually reserve the facility slots in the snapshot.
		// TODO: could potentially improve this if kept track of all items needed to manufacture in a facility
		// and be able to query for that in one call (since makeItems can take a list of items.
		for (Item itemToManufacture : allRequiredItems.keySet()) {
		  ManufacturingFacility.Type facilityType = itemToManufacture.getManufacturingFacilityType();
		  productionRunner.getFacility(facilityType).makeItems(itemToManufacture);
		}
	  } else {
		  // If can't manufacture item, then put reserved items back into production map.
		for (Item subtractedItem : itemsSubtractedFromProduction.keySet()) {
		  int currentNumInProd = itemsInProduction.get(subtractedItem);
		  int numSubtracted = itemsSubtractedFromProduction.get(subtractedItem);
		  itemsInProduction.put(subtractedItem, currentNumInProd + numSubtracted);
		}
		
		// Move on to the next most-expensive item.
		expensiveItemIndex--;
		expensiveItem = itemsSortedByMaxSellingPrice.get(expensiveItemIndex);
	  }
	} while (noMoreFreeSlotsLeft(productionRunner));
  }
  
  private boolean noMoreFreeSlotsLeft(ProductionRunner prodRunner) {
	for (ManufacturingFacility facility : prodRunner.getFacilities().values()) {
	  if (facility.getNumFreeSlots() != 0) return true;
	}
	return false;
  }

  private void tryToManufactureItem(Item requiredItem,
	  int numCopies,
	  Map<Item, Integer> itemsInProduction,
	  Map<Item, Integer> itemsSubtractedFromProduction,
	  Map<ManufacturingFacility.Type, Integer> facilityTypeToNumSlotsMap,
	  Map<Item, Integer> allRequiredItems) {
	int numExisting = 0;
	
	// If item already in production and we're allowed to use it, then use it. Otherwise, add it to manufacturing queue.
	if (itemsInProduction.containsKey(requiredItem)) {
	  int numAvailable = itemsInProduction.get(requiredItem);
	  numExisting = numCopies < numAvailable ? numCopies : numAvailable;

	  // Update map of existing items to remove the ones we're going to use
	  // from future calculations.
	  // However, in the case that this item doens't get made at the end,
	  // we'll have to go back and re-add these
	  // items back to the itemsInProduction map.
	  // This subtraction is done right now because there could be a
	  // sequence of dependencies on the same item.
	  // For example, item A could depend on B and C and item B also depends
	  // on C.
	  itemsInProduction.put(requiredItem, numAvailable - numExisting);
	  itemsSubtractedFromProduction.put(requiredItem, numExisting);
	} else {
	  int numOfItemNeedToManufacture = numCopies - numExisting;
      facilityTypeToNumSlotsMap.put(requiredItem.getManufacturingFacilityType(), numOfItemNeedToManufacture);
	  allRequiredItems.put(requiredItem, numOfItemNeedToManufacture); 
	}

	// Now manufacture recursively for each required ingredient.
	ImmutableMap<Item, Integer> requiredIngredients = requiredItem.getRequiredIngredients();
	for (Item childRequiredIngredient : requiredIngredients.keySet()) {
	  tryToManufactureItem(childRequiredIngredient, requiredIngredients.get(childRequiredIngredient),
		  itemsInProduction, itemsSubtractedFromProduction, facilityTypeToNumSlotsMap, allRequiredItems);
	}
  }
  
  /**
   * Returns which store it's best to buy the next slot.
   * 
   * Basic algorithm is to find the store that's used the most to make other items.
   */
  ManufacturingFacility.Type buyNextSlot() {
	return ManufacturingFacility.Type.FACTORY; // TODO: implement
  }
}
