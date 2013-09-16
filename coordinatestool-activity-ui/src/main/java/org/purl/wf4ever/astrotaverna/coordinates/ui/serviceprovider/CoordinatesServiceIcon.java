package org.purl.wf4ever.astrotaverna.coordinates.ui.serviceprovider;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.purl.wf4ever.astrotaverna.coordinates.CoordinatesActivity;
import org.purl.wf4ever.astrotaverna.coordinates.CoordinatesDegreeActivity;

public class CoordinatesServiceIcon implements ActivityIconSPI {

	private static Icon icon;

	public int canProvideIconScore(Activity<?> activity) {
		if ((activity instanceof CoordinatesActivity)
				|| (activity instanceof CoordinatesDegreeActivity)) {
			return DEFAULT_ICON;
		}
		return NO_ICON;
	}

	public Icon getIcon(Activity<?> activity) {
		return getIcon();
	}

	public static Icon getIcon() {
		if (icon == null) {
			icon = new ImageIcon(
					CoordinatesServiceIcon.class
							.getResource("/NGC_4414_16x16.png"));
		}
		return icon;
	}

}
