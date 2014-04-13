package kaboom.shared.comparators;

import java.util.Comparator;

import kaboom.shared.FormatIdentify;

public class FormatIdentifyComparator implements Comparator<FormatIdentify> {

	@Override
	public int compare(FormatIdentify firstIdentify, FormatIdentify secondIdentify) {

		int firstValue = firstIdentify.getType().getValue();
		int secondValue = secondIdentify.getType().getValue();
		if (firstValue > secondValue) {
			return 1;
		} else if (secondValue > firstValue) {
			return -1;
		}
		return 0;
	}
}
