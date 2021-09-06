package theatre

import java.sql.PreparedStatement
import java.sql.ResultSet
import kotlin.Int
import kotlin.String
import kotlin.Unit
import norm.ParamSetter
import norm.Query
import norm.RowMapper

public data class GetTheatreFromNameParams(
  public val name: String?
)

public class GetTheatreFromNameParamSetter : ParamSetter<GetTheatreFromNameParams> {
  public override fun map(ps: PreparedStatement, params: GetTheatreFromNameParams): Unit {
    ps.setObject(1, params.name)
  }
}

public data class GetTheatreFromNameResult(
  public val id: Int,
  public val name: String
)

public class GetTheatreFromNameRowMapper : RowMapper<GetTheatreFromNameResult> {
  public override fun map(rs: ResultSet): GetTheatreFromNameResult = GetTheatreFromNameResult(
  id = rs.getObject("id") as kotlin.Int,
    name = rs.getObject("name") as kotlin.String)
}

public class GetTheatreFromNameQuery : Query<GetTheatreFromNameParams, GetTheatreFromNameResult> {
  public override val sql: String = """
      |SELECT * FROM theatres WHERE name=?;
      |""".trimMargin()

  public override val mapper: RowMapper<GetTheatreFromNameResult> = GetTheatreFromNameRowMapper()

  public override val paramSetter: ParamSetter<GetTheatreFromNameParams> =
      GetTheatreFromNameParamSetter()
}
