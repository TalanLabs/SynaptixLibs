package com.synaptix.widget.constants;

import java.util.Map;

import com.synaptix.constants.shared.ConstantsWithLookingBundle;
import com.synaptix.constants.shared.DefaultDescription;

@DefaultDescription("Framework : dates")
public interface SynaptixDateConstantsBundle extends ConstantsWithLookingBundle {

	@DefaultStringValue("dd/MM/yyyy")
	@Description("Format date ex : dd/MM/yyyy => 25/01/2012")
	public String displayDateFormat();

	@DefaultStringValue("EEEEE dd MMMMM YYYY")
	@Description("Format date ex : dd/MM/yyyy => 25/01/2012")
	public String displayLongDateFormat();

	@DefaultStringValue("EEE dd MMM YYYY")
	@Description("Format date ex : dd/MM/yyyy => 25/01/2012")
	public String displayShortDateFormat();

	@DefaultStringValue("dd MMM yyyy HH:mm")
	@Description("Format date ex : dd/MM/yyyy => 25/01/2012")
	public String displayMediumDateTimeFormat();

	@DefaultStringValue("dd/MM/yyyy")
	@Description("Format date ex : dd/MM/yyyy => 25/01/2012")
	public String editorDateFormat();

	@DefaultStringValue("dd/MM/yyyy HH:mm")
	@Description("Format date time ex : dd/MM/yyyy HH:mm => 25/01/2012 15:12")
	public String displayDateTimeFormat();

	@DefaultStringValue("dd/MM/yyyy HH:mm:ss")
	@Description("Format date time ex : dd/MM/yyyy HH:mm:ss => 25/01/2012 15:12:30")
	public String displayDateTimeSecondFormat();

	@DefaultStringValue("dd/MM HH:mm")
	@Description("Format date time ex : dd/MM HH:mm => 25/01 15:12")
	public String displayShortDateTimeFormat();

	@DefaultStringValue("dd/MM/yyyy HH:mm")
	@Description("Format date time ex : dd/MM/yyyy HH:mm => 25/01/2012 15:12")
	public String editorDateTimeFormat();

	@DefaultStringValue("HH:mm")
	@Description("Format time ex : HH:mm => 15:12")
	public String displayTimeFormat();

	@DefaultStringValue("HH:mm")
	@Description("Format time ex : HH:mm => 15:12")
	public String editorTimeFormat();

	@DefaultStringValue("HH:mm:ss")
	@Description("Format time ex : HH:mm:ss => 15:12:34")
	public String displayTimeSecondFormat();

	@DefaultStringArrayValue({ "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday" })
	public String[] daysList();

	@DefaultStringMapValue({ "1", "MO", "2", "TU", "3", "WE", "4", "TH", "5", "FR", "6", "SA", "7", "SU" })
	public Map<String, String> shortWeekDays();

	@DefaultStringArrayValue({ "M", "T", "W", "T", "F", "S", "S" })
	public String[] veryShortWeekDays();

	@DefaultStringArrayValue({ "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" })
	public String[] monthsList();

	@DefaultStringValue("Week {0}")
	public String week(int weekOfWeekyear);

	@DefaultStringValue("EEEEE dd MMMMM YYYY")
	@Description("Format date ex : dd/MM/yyyy => 25/01/2012")
	public String displayPlanningOneDayModeDateFormat();

	@DefaultStringValue("EEEEE dd MMMMM YYYY")
	@Description("Format date ex : dd/MM/yyyy => 25/01/2012")
	public String displayPlanningTwoDayModeDateFormat();

	@DefaultStringValue("EEE dd MMM")
	@Description("Format date ex : dd/MM/yyyy => 25/01/2012")
	public String displayPlanningWeekModeDateFormat();

	@DefaultStringValue("{0}j {1,number,00}:{2,number,00}")
	public String displayPlanningTime(int d, int h, int m);

	@DefaultStringValue("{0}j {1,number,00}:{2,number,00}:{3,number,00}")
	public String displayPlanningTimeSecond(int d, int h, int m, int s);

	@DefaultStringValue("{0}min")
	public String displayPlanningMinutes(int m);

	@DefaultStringValue("{0,number,00}:{1,number,00}")
	public String displayPlanningHours(int h, int m);

	@DefaultStringValue("Timezone")
	public String timezone();

	@DefaultStringValue("{0}j")
	public String daysShort(int days);

	@DefaultStringValue("{0}h")
	public String hoursShort(int days);

	@DefaultStringValue("{0}j")
	public String displayPlanningDays(int d);

	@DefaultStringValue("MM/yyyy")
	@Description("Format date ex : MM/yyyy => 12/2012")
	public String monthEditor();

	@DefaultStringValue("MMMMM yyyy")
	public String hierarchicalMonthDisplay();

}
