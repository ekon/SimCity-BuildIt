package ekon.simcity.city;

import com.google.common.base.MoreObjects;

public enum FactoryType {
  SMALL_FACTORY(3);
  
  private final int numSlots;
  private FactoryType(int numSlots) {
	this.numSlots = numSlots;
  }

  public int getNumSlots() {
    return numSlots;
  }

  @Override
  public String toString() {
	return MoreObjects.toStringHelper(this)
		.add("name", name())
		.add("numSlots", numSlots)
		.toString();
  }
}
