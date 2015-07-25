package ekon.simcity.city;

public class ElementFactory {
  private static final ElementSpec ROAD_SPEC = new ElementSpec(ElementType.ROAD, 1, 1);
  private static final ElementSpec HOUSE_SPEC = new ElementSpec(ElementType.HOUSE, 2, 2);

  private static final ServiceSpec SMALL_FIRE_STATION = new ServiceSpec(ElementType.FIRE_STATION, 1, 1, 6, 8);
  private static final ServiceSpec SMALL_POLICE_STATION = new ServiceSpec(ElementType.POLICE_STATION, 1, 1, 6, 7);
  private static final ServiceSpec SMALL_FOUNTAIN_PARK = new ServiceSpec(ElementType.PARK, 1, 1, 8, 8);

  public static ServiceSpec getServiceSpec(ElementType type) {
	switch (type) {
	  case FIRE_STATION:
		return SMALL_FIRE_STATION;
	  case POLICE_STATION:
		return SMALL_POLICE_STATION;
	  case PARK:
		return SMALL_FOUNTAIN_PARK;
	  default:
		throw new IllegalArgumentException("Service type " + type + " not supported");
	}
  }

  public static GridElement getElement(ElementType type, Position position) {
	switch (type) {
	  case ROAD:
		return new GridElement(ROAD_SPEC, position);
	  case HOUSE:
		return new GridElement(HOUSE_SPEC, position);
	  case FIRE_STATION:
	  case POLICE_STATION:
	  case PARK:
		ServiceSpec serviceSpec = getServiceSpec(type);
		return new GridElement(serviceSpec, position);
	  default:
		throw new IllegalArgumentException("Service type " + type + " not supported");
	}
  }

  private ElementFactory() {}
}
