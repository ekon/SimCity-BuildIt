package ekon.simcity.city;

import java.util.Arrays;
import java.util.List;

import com.google.common.base.MoreObjects;

public enum ElementType {
  ROAD(ElementConstraint.NEAR_ROAD, ElementConstraint.DOES_NOT_OVERLAP),
  HOUSE(ElementConstraint.NEAR_ROAD, ElementConstraint.DOES_NOT_OVERLAP, ElementConstraint.COVERED_BY_REQUIRED_SERVICE),
  FIRE_STATION(ElementConstraint.NEAR_ROAD, ElementConstraint.DOES_NOT_OVERLAP),
  POLICE_STATION(ElementConstraint.NEAR_ROAD, ElementConstraint.DOES_NOT_OVERLAP),
  PARK(ElementConstraint.NEAR_ROAD, ElementConstraint.DOES_NOT_OVERLAP);

  private List<ElementConstraint> constraints;
  private ElementType(ElementConstraint... constraints) {
	this.constraints = Arrays.asList(constraints);
  }
  
  List<ElementConstraint> getConstraints() {
	return constraints;
  }

  @Override
  public String toString() {
	return MoreObjects.toStringHelper(this)
		.add("name", name())
		.add("constraints", constraints)
		.toString();
  }
}
