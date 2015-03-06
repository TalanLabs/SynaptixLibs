import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.synaptix.component.annotation.SynaptixComponent;
import com.synaptix.entity.IEntity;

@Entity
@Table(name = "T_TOTO")
@SynaptixComponent
public interface IToto extends IEntity {

	public void setText(String text);

	@Column(name = "TEXT")
	public String getText();

	@Computed(Calcule2.class)
	public String getCalcule(int v);

	@Column(name = "TOTO")
	@DefaultValue("'0'")
	public boolean isToto();

	public void setToto(boolean toto);

	public IToto getRien();

	public void setRien(IToto entity);

}
