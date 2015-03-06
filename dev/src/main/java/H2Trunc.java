import java.sql.Date;
import java.sql.Timestamp;

public class H2Trunc {

	public final static Date trunc(Timestamp timeStamp) {
		return new Date(timeStamp.getTime());
	}
}
