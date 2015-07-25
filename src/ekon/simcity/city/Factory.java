package ekon.simcity.city;

import java.util.Objects;

import com.google.common.base.MoreObjects;

public class Factory extends ManufacturingFacility {

  private final FactoryType type;

  public Factory(FactoryType type) {
	super(ManufacturingFacility.Type.FACTORY, type.getNumSlots());
	this.type = type;
  }

  public FactoryType getFactoryType() {
    return type;
  }
  
  @Override
  public void addSlot() {
	throw new UnsupportedOperationException("Factories cannot have slots added to them. Need to buy a new factory if want more slots.");
  }

  public int getNumEmptySlots() {
	return 1; // TODO: implement
  }

  @Override
  public String toString() {
	return MoreObjects.toStringHelper(this)
		.addValue(super.toString())
		.add("type", type)
		.toString();
  }

  @Override
  public int hashCode() {
	return Objects.hash(super.hashCode(), type);
  }

  @Override
  public boolean equals(Object other) {
	if (!(other instanceof Factory)) return false;
	Factory o = (Factory)other;
	return super.equals(other)
		&& Objects.equals(getType(), o.getType());
  }
}
