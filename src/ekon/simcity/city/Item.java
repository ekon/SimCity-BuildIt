package ekon.simcity.city;

import java.util.Comparator;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableMap;

public enum Item {
  METAL(ManufacturingFacility.Type.FACTORY, 1, 10),
  WOOD(ManufacturingFacility.Type.FACTORY, 3, 20),
  PLASTIC(ManufacturingFacility.Type.FACTORY, 9, 25),
  SEEDS(ManufacturingFacility.Type.FACTORY, 20, 30),
  MINERALS(ManufacturingFacility.Type.FACTORY, 30, 40),
  CHEMICALS(ManufacturingFacility.Type.FACTORY, 120, 60),
  TEXTILES(ManufacturingFacility.Type.FACTORY, 180, 90),
  SUGAR_AND_SPICES(ManufacturingFacility.Type.FACTORY, 240, 119), // TODO: double check price!
  GLASS(ManufacturingFacility.Type.FACTORY, 300, 120),
  ANIMAL_FEED(ManufacturingFacility.Type.FACTORY, 360, 120),

  NAILS(ManufacturingFacility.Type.BUILDING_SUPPLY_STORE, 5, 80, ImmutableMap.of(METAL, 2)),
  PLANKS(ManufacturingFacility.Type.BUILDING_SUPPLY_STORE, 30, 120, ImmutableMap.of(WOOD, 2)),
  BRIKS(ManufacturingFacility.Type.BUILDING_SUPPLY_STORE, 20, 190, ImmutableMap.of(MINERALS, 2)),
  CEMENT(ManufacturingFacility.Type.BUILDING_SUPPLY_STORE, 50, 440, ImmutableMap.of(MINERALS, 2, CHEMICALS, 1)), // TODO: double-check if selling price is same as glue
  GLUE(ManufacturingFacility.Type.BUILDING_SUPPLY_STORE, 60, 440, ImmutableMap.of(PLASTIC, 1, CHEMICALS, 2)), // TODO: double-check if selling price is same as cement

  HAMMER(ManufacturingFacility.Type.HARDWARE_STORE, 14, 90, ImmutableMap.of(METAL, 1, WOOD, 1)),
  MEASURING_TAPE(ManufacturingFacility.Type.HARDWARE_STORE, 20, 110, ImmutableMap.of(METAL, 1, PLASTIC, 1)),
  SHOVEL(ManufacturingFacility.Type.HARDWARE_STORE, 30, 150, ImmutableMap.of(METAL, 1, WOOD, 1, PLASTIC, 1)),
  COOKING_UTENSIL(ManufacturingFacility.Type.HARDWARE_STORE, 45, 71, ImmutableMap.of(METAL, 2, WOOD, 2, PLASTIC, 2)),
  
  CHAIR(ManufacturingFacility.Type.FURNITURE_STORE, 20, 300, ImmutableMap.of(WOOD, 2, NAILS, 1, HAMMER, 1)),
  TABLE(ManufacturingFacility.Type.FURNITURE_STORE, 30, 500, ImmutableMap.of(PLANKS, 2, NAILS, 2, HAMMER, 1)),
  
  GRASS(ManufacturingFacility.Type.GARDENING_SUPPLIES, 30, 310, ImmutableMap.of(SEEDS, 1, SHOVEL, 1)),
  TREE_SAPLING(ManufacturingFacility.Type.GARDENING_SUPPLIES, 90, 420, ImmutableMap.of(SEEDS, 2, SHOVEL, 1)),
  GARDEN_FURNITURE(ManufacturingFacility.Type.GARDENING_SUPPLIES, 135, 820, ImmutableMap.of(PLANKS, 2, PLASTIC, 2, TEXTILES, 2)),
  
  VEGETABLES(ManufacturingFacility.Type.FARMERS_MARKET, 20, 160, ImmutableMap.of(SEEDS, 2)),
  FLOUR_BAG(ManufacturingFacility.Type.FARMERS_MARKET, 30, 320, ImmutableMap.of(SEEDS, 2, TEXTILES, 1)), // TODO: fix selling price (set random one for now) 
  FRUITS_AND_BERRIES(ManufacturingFacility.Type.FARMERS_MARKET, 90, 730, ImmutableMap.of(SEEDS, 2, TREE_SAPLING, 1)),
  CREAM(ManufacturingFacility.Type.FARMERS_MARKET, 75, 440, ImmutableMap.of(ANIMAL_FEED, 1)),
  
  DONUTS(ManufacturingFacility.Type.DONUT_SHOP, 45, 950, ImmutableMap.of(FLOUR_BAG, 1, SUGAR_AND_SPICES, 1)),
  GREEN_SMOOTHIE(ManufacturingFacility.Type.DONUT_SHOP, 30, 1150, ImmutableMap.of(VEGETABLES, 1, FRUITS_AND_BERRIES, 1)),
  BREAD_ROLL(ManufacturingFacility.Type.DONUT_SHOP, 60, 1840, ImmutableMap.of(FLOUR_BAG, 2, CREAM, 1)),
  
  CAP(ManufacturingFacility.Type.FASHION_STORE, 60, 600, ImmutableMap.of(TEXTILES, 2, MEASURING_TAPE, 1)),
  SHOES(ManufacturingFacility.Type.FASHION_STORE, 75, 980, ImmutableMap.of(TEXTILES, 2, PLASTIC, 1, GLUE, 1)),
  WATCH(ManufacturingFacility.Type.FASHION_STORE, 90, 580, ImmutableMap.of(PLASTIC, 1, GLASS, 1, CHEMICALS, 1));
  
  private final ManufacturingFacility.Type manufacturingFacilityType;
  private final int minutesToMake;
  private final int maxSellingPrice;
  private final ImmutableMap<Item, Integer> requiredIngredients;

  private Item(ManufacturingFacility.Type storeType, int minutesToMake, int maxSellingPrice) {
	this(storeType, minutesToMake, maxSellingPrice, ImmutableMap.of());
  }

  private Item(ManufacturingFacility.Type manufacturingFacilityType, int minutesToMake, int maxSellingPrice, ImmutableMap<Item, Integer> requiredIngredients) {
	this.manufacturingFacilityType = manufacturingFacilityType;
	this.minutesToMake = minutesToMake;
	this.maxSellingPrice = maxSellingPrice;
	this.requiredIngredients = requiredIngredients;
  }

  public ManufacturingFacility.Type getManufacturingFacilityType() {
	return manufacturingFacilityType;
  }

  public int getMinutesToMake() {
	return minutesToMake;
  }

  public int getMaxSellingPrice() {
	return maxSellingPrice;
  }

  public ImmutableMap<Item, Integer> getRequiredIngredients() {
    return requiredIngredients;
  }

  @Override
  public String toString() {
	return MoreObjects.toStringHelper(this)
		.add("name", name())
		.add("manufacturingFacilityType", manufacturingFacilityType)
		.add("minutesToMake", minutesToMake)
		.add("maxSellingPrice", maxSellingPrice)
		.add("requiredIngredients", requiredIngredients)
		.toString();
  }
  
  public static class MaxSellingPriceComparator implements Comparator<Item> {
	@Override
	public int compare(Item i1, Item i2) {
	  return Integer.compare(i1.maxSellingPrice, i2.maxSellingPrice);
	}
  }
  
  public static class MinutesToMakeComparactor implements Comparator<Item> {
	@Override
	public int compare(Item i1, Item i2) {
	  return Integer.compare(i1.minutesToMake, i2.minutesToMake);
	}
  }
}
