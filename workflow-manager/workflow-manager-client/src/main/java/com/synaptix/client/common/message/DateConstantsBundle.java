package com.synaptix.client.common.message;

import java.util.Map;

import com.synaptix.constants.shared.ConstantsWithLookingBundle;
import com.synaptix.constants.shared.DefaultDescription;

@DefaultDescription("Dates")
public interface DateConstantsBundle extends ConstantsWithLookingBundle {

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

	@DefaultStringValue("dd/MM HH:mm")
	@Description("Format date time ex : dd/MM HH:mm => 25/01 15:12")
	public String displayShortDateTimeFormat();

	@DefaultStringValue("dd/MM/yyyy HH:mm")
	@Description("Format date time ex : dd/MM/yyyy HH:mm => 25/01/2012 15:12")
	public String editorDateTimeFormat();

	@DefaultStringValue("yyyy-MM-dd_HHmmss")
	@Description("Format date time ex : yyyy-MM-dd_HHmmss => 2012-01-25_151259")
	public String fileDateTimeFormat();

	@DefaultStringValue("HH:mm")
	@Description("Format time ex : HH:mm => 15:12")
	public String displayTimeFormat();

	@DefaultStringValue("HH:mm")
	@Description("Format time ex : HH:mm => 15:12")
	public String editorTimeFormat();

	@DefaultStringValue("HH:mm:ss")
	@Description("Format time ex : HH:mm:ss => 15:12:34")
	public String displayTimeSecondFormat();

	@DefaultStringArrayValue({ "Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi", "Dimanche" })
	public String[] daysList();

	@DefaultStringMapValue({ "1", "LU", "2", "MA", "3", "ME", "4", "JE", "5", "VE", "6", "SA", "7", "DI" })
	public Map<String, String> shortWeekDays();

	@DefaultStringArrayValue({ "Lu", "Ma", "Me", "Je", "Ve", "Sa", "Di" })
	public String[] shortDaysList();

	@DefaultStringValue("Semaine {0}")
	public String week(int weekOfWeekyear);

	@DefaultStringValue("S {0}")
	public String week_initial(int weekOfWeekyear);

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

	@DefaultStringValue("{0}j {1,number,00}h")
	public String displayPlanningDaysHours(int d, int h);

	@DefaultStringValue("jours")
	public String days();

	@DefaultStringValue("heures")
	public String hours();

	@DefaultStringValue("minutes")
	public String minutes();

	@DefaultStringValue("dd-MM")
	@Description("Format date ex : dd-MM => 25-01")
	public String displayDayMonthFormat();

}
