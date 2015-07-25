package ekon.simcity.city;

import java.util.Objects;

import com.google.common.base.MoreObjects;

class GridElement {
  private ElementSpec spec;
  private Position position;

  GridElement(ElementSpec spec, Position position) {
	this.position = position;
	this.spec = spec;
  }

  ElementSpec getSpec() {
	return spec;
  }

  Position getPosition() {
	return position;
  }

  @Override
  public String toString() {
	return MoreObjects.toStringHelper(this)
		.add("position", position)
		.add("spec", spec)
		.toString();
  }
  
  @Override
  public int hashCode() {
	return Objects.hash(position, spec);
  }
  
  @Override
  public boolean equals(Object other) {
	if (!(other instanceof GridElement)) return false;
	GridElement o = (GridElement)other;
	return Objects.equals(getPosition(), o.getPosition())
		&& Objects.equals(getSpec(), o.getSpec());
  }
}
