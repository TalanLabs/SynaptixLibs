package toto;

import java.util.List;

import com.synaptix.gwt.annotation.SynaptixGWTComponent;
import com.synaptix.gwt.shared.component.IComponentDto;

public class MainToto {

	public static void main(String[] args) {
		ITata tata = new TataBuilder().meaning("toto").build();

		System.out.println(tata.straightGetPropertyNames());

		System.out.println(tata.straightGetProperty("meaning"));

		// System.out.println(TotoFields.id().name());
		// System.out.println(TotoFields.tata().name());
		// System.out.println(TotoFields.tata().dot().meaning().name());
		// System.out.println(TotoFields.tata().dot().titi().name());
		// System.out.println(TotoFields.tata().dot().titi().dot().meaning().name());
		// System.out.println(TataFields.titi().name());
		//
		// System.out.println(TotoDtoFields.meaning().name());
		// System.out.println(TotoDtoFields.meaning());
		// System.out.println(TotoDtoFields.tataDto().name());
		// System.out.println(TotoDtoFields.tataDto().dot().meaning());
		// System.out.println(TotoDtoFields.tataDto().dot().titiDto().name());
		// System.out.println(TotoDtoFields.tataDto().dot().titiDto().dot().meaning().name());
	}

	@SynaptixGWTComponent
	public interface ITest extends ITest1<ITitiDto>, ITest3<ITitiDto> {

	}

	@SynaptixGWTComponent(createBuilder = false)
	public interface ITest1<F extends Object> extends IComponentDto {

		public List<F> getName1();

		public void setName1(List<F> name);

		public String getMeaning();

		public void setMeaning(String meaning);
	}

	@SynaptixGWTComponent(createBuilder = false)
	public interface ITest2<F extends Object, G extends Object> extends IComponentDto, ITest3<G> {

		public List<F> getName2();

		public void setName2(List<F> name);

		public G getName3();

		public void setName3(G name);

	}

	@SynaptixGWTComponent(createBuilder = false)
	public interface ITest3<F extends Object> extends IComponentDto {

		public F getName4();

		public void setName4(F name);

		public String getMeaning();

		public void setMeaning(String meaning);
	}
}
