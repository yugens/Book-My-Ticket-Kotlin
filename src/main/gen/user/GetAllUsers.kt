package user

import java.sql.PreparedStatement
import java.sql.ResultSet
import kotlin.Int
import kotlin.String
import kotlin.Unit
import norm.ParamSetter
import norm.Query
import norm.RowMapper

public class GetAllUsersParams

public class GetAllUsersParamSetter : ParamSetter<GetAllUsersParams> {
  public override fun map(ps: PreparedStatement, params: GetAllUsersParams): Unit {
  }
}

public data class GetAllUsersResult(
  public val id: Int,
  public val name: String,
  public val email: String
)

public class GetAllUsersRowMapper : RowMapper<GetAllUsersResult> {
  public override fun map(rs: ResultSet): GetAllUsersResult = GetAllUsersResult(
  id = rs.getObject("id") as kotlin.Int,
    name = rs.getObject("name") as kotlin.String,
    email = rs.getObject("email") as kotlin.String)
}

public class GetAllUsersQuery : Query<GetAllUsersParams, GetAllUsersResult> {
  public override val sql: String = """
      |SELECT * FROM users
      |""".trimMargin()

  public override val mapper: RowMapper<GetAllUsersResult> = GetAllUsersRowMapper()

  public override val paramSetter: ParamSetter<GetAllUsersParams> = GetAllUsersParamSetter()
}
