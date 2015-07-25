package ekon.simcity.city;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

/**
 * Responsible for running the city's stores and factories.
 */
public class ProductionRunner {

  private Map<ManufacturingFacility.Type, ManufacturingFacility> facilities;

  /** Keeps track of the currently available items for manufacturing since not all items are unlocked at once. */
  private Multimap<ManufacturingFacility.Type, Item> unlockedItems;

  public ProductionRunner() {
	facilities = new HashMap<>();
	unlockedItems = ArrayListMultimap.create();
  }

  /** Returns a read-only (immutable) copy of the currently available items for manufacturing. */
  public ImmutableMultimap<ManufacturingFacility.Type, Item> getUnlockedItems() {
	return ImmutableMultimap.copyOf(unlockedItems);
  }

  public void unlockItem(Item item) {
	if (unlockedItems.containsValue(item)) throw new IllegalStateException("Item " + item + " is already unlocked.");
	unlockedItems.put(item.getManufacturingFacilityType(), item);
  }

  public void addFacility(ManufacturingFacility facility) {
	ManufacturingFacility.Type type = facility.getType();
	
	// Only factories can be added more than once. All stores can only have a single instance.
	if ((type != ManufacturingFacility.Type.FACTORY) 
		&& facilities.containsKey(type)) throw new IllegalStateException("Facility of type " + type + " already exists.");

	this.facilities.put(type, facility);
  }
  
  public void addSlot(ManufacturingFacility.Type type) {
	if (!facilities.containsKey(type)) throw new IllegalStateException("Facility of type " + type + " does not exist");
	facilities.get(type).addSlot();
  }

  public boolean hasFacility(ManufacturingFacility.Type type) {
	return facilities.containsKey(type);
  }

  /**
   * Returns an immutable copy of the current facilities.
   * Note: changes to this list will not be reflected properly. Use {@link #addSlot} or {@link #addFacility} for modifying this list instead.
   */
  public ImmutableMap<ManufacturingFacility.Type, ManufacturingFacility> getFacilities() {
	return ImmutableMap.copyOf(facilities);
  }
  
  public ManufacturingFacility getFacility(ManufacturingFacility.Type type) {
	if (!facilities.containsKey(type)) throw new IllegalStateException("Facility of type " + type + " does not exist");
	return facilities.get(type);
  }

  @Override
  public String toString() {
	return MoreObjects.toStringHelper(this)
		.add("facilities", facilities)
		.toString();
  }

  @Override
  public int hashCode() {
	return Objects.hash(facilities);
  }

  @Override
  public boolean equals(Object other) {
	if (!(other instanceof ProductionRunner)) return false;
	ProductionRunner o = (ProductionRunner)other;
	return Objects.equals(facilities, o.facilities); // TODO: fix to work for maps.
  }
}
