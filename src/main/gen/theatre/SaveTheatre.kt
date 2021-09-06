package theatre

import java.sql.PreparedStatement
import java.sql.ResultSet
import kotlin.Int
import kotlin.String
import kotlin.Unit
import norm.ParamSetter
import norm.Query
import norm.RowMapper

public data class SaveTheatreParams(
  public val name: String?
)

public class SaveTheatreParamSetter : ParamSetter<SaveTheatreParams> {
  public override fun map(ps: PreparedStatement, params: SaveTheatreParams): Unit {
    ps.setObject(1, params.name)
  }
}

public data class SaveTheatreResult(
  public val id: Int,
  public val name: String
)

public class SaveTheatreRowMapper : RowMapper<SaveTheatreResult> {
  public override fun map(rs: ResultSet): SaveTheatreResult = SaveTheatreResult(
  id = rs.getObject("id") as kotlin.Int,
    name = rs.getObject("name") as kotlin.String)
}

public class SaveTheatreQuery : Query<SaveTheatreParams, SaveTheatreResult> {
  public override val sql: String = """
      |INSERT INTO theatres(name)
      |VALUES(?)
      |returning *;
      |""".trimMargin()

  public override val mapper: RowMapper<SaveTheatreResult> = SaveTheatreRowMapper()

  public override val paramSetter: ParamSetter<SaveTheatreParams> = SaveTheatreParamSetter()
}
