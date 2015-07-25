package ekon.simcity.city;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.google.common.base.MoreObjects;

public class ManufacturingFacility {
  public enum Type {
	  FACTORY,
	  BUILDING_SUPPLY_STORE,
	  HARDWARE_STORE,
	  FURNITURE_STORE,
	  FARMERS_MARKET,
	  GARDENING_SUPPLIES,
	  DONUT_SHOP,
	  FASHION_STORE;
	}

  private final Type type;
  private List<Slot> slots; // Note: not thread-safe

  public ManufacturingFacility(Type type, int capacity) {
	this.type = type;
	this.slots = new ArrayList<>(capacity);
	for (int i = 0; i < capacity; i++) slots.add(new Slot());
  }

  public Type getType() {
    return type;
  }

  public List<Slot> getSlots() {
    return slots;
  }
  
  private List<Slot> getFreeSlots() {
	List<Slot> freeSlots = new ArrayList<>();
	for (Slot slot : slots) {
	  if (!slot.hasItem()) freeSlots.add(slot);
	}
	return freeSlots;
  }

  public int getNumFreeSlots() {
	return getFreeSlots().size();
  }
  
  void addSlot() {
	slots.add(new Slot());
  }

  // TODO: not sure if this should be overridden or partially overridden so that we can reuse the check here.
  public void makeItems(Item... items) {
	// Verify empty slots exist.
	if (getNumFreeSlots() < items.length) {
	  throw new IllegalStateException("Not enough free slots to make [" + items + "] in s " + this);
	}
	
	for (Item item : items) {
	  getFreeSlots().get(0).manufactureItem(item);
	}
  }

  @Override
  public String toString() {
	return MoreObjects.toStringHelper(this)
		.add("type", type)
		.add("slots", slots)
		.toString();
  }

  @Override
  public int hashCode() {
	return Objects.hash(type, slots); // TODO: fix to work for lists.
  }

  @Override
  public boolean equals(Object other) {
	if (!(other instanceof ManufacturingFacility)) return false;
	ManufacturingFacility o = (ManufacturingFacility)other;
	return Objects.equals(getType(), o.getType())
		&& Objects.equals(getSlots(), o.getSlots()); // TODO: fix to work for lists.
  }

  public static class Slot {
	private Item item;
//	private final long startTime; // TODO: not sure about using a long, probably better to use some time class.

	public void manufactureItem(Item itemToManufacture) {
	  if (item != null) {
		throw new IllegalStateException("Cannot manufacture " + itemToManufacture + " because something is already being manufatured in this slot " + item);
	  }
	  this.item = itemToManufacture;
	}
	
	public boolean hasItem() {
	  return item != null;
	}
	
	/** Returns the item being manufactured at this slot, if any. */
	public Item getItem() {
	  if (item == null) throw new IllegalStateException("No item in slot. Please call hasItem first to check.");
 	  return item;
	}
  }
}
