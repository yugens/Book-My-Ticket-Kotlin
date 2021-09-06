package theatre

import java.sql.PreparedStatement
import java.sql.ResultSet
import kotlin.Int
import kotlin.String
import kotlin.Unit
import norm.ParamSetter
import norm.Query
import norm.RowMapper

public class GetAllTheatresParams

public class GetAllTheatresParamSetter : ParamSetter<GetAllTheatresParams> {
  public override fun map(ps: PreparedStatement, params: GetAllTheatresParams): Unit {
  }
}

public data class GetAllTheatresResult(
  public val id: Int,
  public val name: String
)

public class GetAllTheatresRowMapper : RowMapper<GetAllTheatresResult> {
  public override fun map(rs: ResultSet): GetAllTheatresResult = GetAllTheatresResult(
  id = rs.getObject("id") as kotlin.Int,
    name = rs.getObject("name") as kotlin.String)
}

public class GetAllTheatresQuery : Query<GetAllTheatresParams, GetAllTheatresResult> {
  public override val sql: String = """
      |SELECT * FROM theatres
      |""".trimMargin()

  public override val mapper: RowMapper<GetAllTheatresResult> = GetAllTheatresRowMapper()

  public override val paramSetter: ParamSetter<GetAllTheatresParams> = GetAllTheatresParamSetter()
}
