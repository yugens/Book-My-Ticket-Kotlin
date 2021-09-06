package user

import java.sql.PreparedStatement
import java.sql.ResultSet
import kotlin.Int
import kotlin.String
import kotlin.Unit
import norm.ParamSetter
import norm.Query
import norm.RowMapper

public data class AddUserParams(
  public val name: String?,
  public val email: String?
)

public class AddUserParamSetter : ParamSetter<AddUserParams> {
  public override fun map(ps: PreparedStatement, params: AddUserParams): Unit {
    ps.setObject(1, params.name)
    ps.setObject(2, params.email)
  }
}

public data class AddUserResult(
  public val id: Int,
  public val name: String,
  public val email: String
)

public class AddUserRowMapper : RowMapper<AddUserResult> {
  public override fun map(rs: ResultSet): AddUserResult = AddUserResult(
  id = rs.getObject("id") as kotlin.Int,
    name = rs.getObject("name") as kotlin.String,
    email = rs.getObject("email") as kotlin.String)
}

public class AddUserQuery : Query<AddUserParams, AddUserResult> {
  public override val sql: String = """
      |INSERT INTO users(name, email)
      |VALUES ( ?, ?)
      |returning *;
      |""".trimMargin()

  public override val mapper: RowMapper<AddUserResult> = AddUserRowMapper()

  public override val paramSetter: ParamSetter<AddUserParams> = AddUserParamSetter()
}
