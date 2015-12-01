package workflow.handler;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.apache.ibatis.type.TypeException;

import oracle.jdbc.OracleTypes;

@MappedTypes(URI.class)
public class URITypeHandler extends BaseTypeHandler<URI> {

	@Override
	public void setParameter(PreparedStatement ps, int i, URI parameter, JdbcType jdbcType) throws SQLException {
		if (parameter == null) {
			try {
				ps.setNull(i, OracleTypes.VARCHAR);
			} catch (SQLException e) {
				throw new TypeException("Error setting null for parameter #" + i + " with JdbcType " + jdbcType + " . "
						+ "Try setting a different JdbcType for this parameter or a different jdbcTypeForNull configuration property. " + "Cause: " + e, e);
			}
		} else {
			setNonNullParameter(ps, i, parameter, jdbcType);
		}
	}

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, URI parameter, JdbcType jdbcType) throws SQLException {
		ps.setString(i, parameter.toString());
	}

	@Override
	public URI getNullableResult(ResultSet rs, String columnName) throws SQLException {
		return parse(rs.getString(columnName));
	}

	@Override
	public URI getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		return parse(rs.getString(columnIndex));
	}

	@Override
	public URI getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		return parse(cs.getString(columnIndex));
	}

	private URI parse(String name) throws SQLException {
		if (name != null) {
			try {
				return new URI(name);
			} catch (URISyntaxException e) {
				throw new SQLException(e);
			}
		}
		return null;
	}
}
