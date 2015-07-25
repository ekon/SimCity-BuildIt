package ekon.simcity.city;

public class Store extends ManufacturingFacility {

  public Store(ManufacturingFacility.Type type, int capacity) {
	super(type, capacity);
  }
  
  // Add logic for how a store sequences production
  // need to figure out sequencing - would items move through slots as they become available?
  // maybe have 2 types of slots (active slots - the ones factories have and the first one for a store) and holding slots with a priority order.

}
