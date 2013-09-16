package org.purl.wf4ever.astrotaverna.coordinates.ui.view;

import java.util.Arrays;
import java.util.List;

import org.purl.wf4ever.astrotaverna.coordinates.CoordinatesActivity;


public class CoordinatesActivityContextViewFactory implements
		ContextualViewFactory<CoordinatesActivity> {

	public boolean canHandle(Object selection) {
		return selection instanceof CoordinatesActivity;
	}

	public List<ContextualView> getViews(CoordinatesActivity selection) {
		return Arrays.<ContextualView>asList(new CoordinatesContextualView(selection));
	}
	
}
