package ekon.simcity.city;

import java.util.Objects;

public class ServiceSpec extends ElementSpec implements HasCoverageArea {
  private int coverageWidth, coverageHeight;

  ServiceSpec(ElementType type, int width, int height, int coverageWidth, int coverageHeight) {
	super(type, width, height);
	this.coverageWidth = coverageWidth;
	this.coverageHeight = coverageHeight;
  }

  @Override
  public int getCoverageWidth() {
	return coverageWidth;
  }

  @Override
  public int getCoverageHeight() {
	return coverageHeight;
  }

  @Override
  public String toString() {
	return "\nServiceSpec [" + super.toString() + ", coverageWidth: " + coverageWidth + ", coverageHeight: "
		+ coverageHeight + "]";
  }

  @Override
  public int hashCode() {
	return Objects.hash(super.hashCode(), coverageWidth, coverageHeight);
  }

  @Override
  public boolean equals(Object other) {
	if (!(other instanceof ServiceSpec)) return false;
	ServiceSpec o = (ServiceSpec)other;
	return super.equals(other)
		&& Objects.equals(getCoverageWidth(), o.getCoverageWidth())
		&& Objects.equals(getCoverageHeight(), o.getCoverageHeight());
  }
}
