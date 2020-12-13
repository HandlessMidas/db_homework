import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.jodatime.date
import org.jetbrains.exposed.sql.transactions.transaction
import org.postgresql.Driver

object Course : Table("course") {
    val courseId = integer("course_id").autoIncrement().uniqueIndex()
    val courseName = text("course_name").nullable()

    override val primaryKey = PrimaryKey(courseId, name = "course_pkey")
}
object Faculty : Table("faculty") {
    val facultyId = integer("faculty_id").autoIncrement().uniqueIndex()
    val facultyName = text("faculty_name").nullable()

    override val primaryKey = PrimaryKey(facultyId, name = "faculty_pkey")
}
object Student : Table("student") {
    val studentId = integer("student_id").autoIncrement().uniqueIndex()
    val studentNumber = text("student_number")
    val lastName = text("last_name")
    val firstName = text("first_name")
    val gender = integer("gender")
    val birthday = date("birthday").nullable()

    override val primaryKey = PrimaryKey(studentId, name = "student_pkey")
}
object StudentGrade : Table("student_grade") {
    val studentGradeId = integer("student_grade_id").autoIncrement().uniqueIndex()
    val studentId = integer("student_id").references(Student.studentId)
    val courseId = integer("course_id").references(Course.courseId)
    val grade = integer("grade")

    override val primaryKey = PrimaryKey(studentGradeId, name = "student_grade_pkey")
}
object StudentUnit : Table("student_unit") {
    val studentUnitId = integer("student_unit_id").autoIncrement().uniqueIndex()
    val unitId = integer("unit_id").references(Unit.unitId)
    val studentId = integer("student_id").references(Student.studentId)
    val isHead = bool("is_head")

    override val primaryKey = PrimaryKey(studentUnitId)
}
object Teacher : Table("teacher") {
    val teacherId = integer("teacher_id").autoIncrement().uniqueIndex()
    val lastName = text("last_name")
    val firstName = text("first_name")
    val birthday = date("birthday").nullable()
    val curator_id = integer("curator_id").references(teacherId).nullable()

    override val primaryKey = PrimaryKey(teacherId, name = "teacher_pkey")
}
object TeacherCourseFaculty : Table("teacher_course_faculty") {
    val teacherCourseFacultyId = integer("teacher_course_faculty_id").autoIncrement().uniqueIndex()
    val teacherId = integer("teacher_id").references(Teacher.teacherId)
    val courseId = integer("course_id").references(Course.courseId)
    val facultyId = integer("faculty_id").references(Faculty.facultyId)
    val duration = double("duration").nullable()

    override val primaryKey = PrimaryKey(teacherCourseFacultyId, name = "teacher_course_faculty_pkey")
}
object Unit : Table("unit") {
    val unitId = integer("unit_id").autoIncrement().uniqueIndex()
    val unitNumber = text("unit_number").nullable()
    val facultyId = integer("faculty_id").references(Faculty.facultyId)

    override val primaryKey = PrimaryKey(unitId, name = "unit_pkey")
}

object Config {
    val url = "jdbc:postgresql://localhost:5432/postgres"
    val user = "postgres"
    val password = "postgres"
}

fun main(args: Array<String>) {
    Database.connect(Config.url, driver = "org.postgresql.Driver", user = Config.user, password = Config.password)


    transaction {
        //addLogger(StdOutSqlLogger)
        if (args[0] == "c") {
            Course.selectAll().orderBy(Course.courseName to SortOrder.ASC).forEach{
                print(it[Course.courseName] + "\n")
            }
        }
        if (args[0] == "s") {
            Student.slice(Student.firstName, Student.lastName).selectAll().forEach {
                print(it[Student.lastName] + ' ' + it[Student.firstName] + "\n")
            }
        }
        if (args[0] == "g") {
            if (args.size == 2) {
                val courseName = args[1]
                StudentGrade.select {
                    val course = Course.select { Course.courseName eq courseName }.first()
                    StudentGrade.courseId eq course[Course.courseId]
                }.map {
                    val student = Student.select { Student.studentId eq it[StudentGrade.studentId] }.first()
                    print(student[Student.lastName] + ' ' + student[Student.firstName] + ' ' + it[StudentGrade.grade] + "\n")
                }
            } else {
                val courseName = args[1]
                val lastName = args[2]
                StudentGrade.select {
                    val course = Course.select { Course.courseName eq courseName }.first()
                    val student = Student.select { Student.lastName eq lastName }.first()
                    (StudentGrade.courseId eq course[Course.courseId])  and (StudentGrade.studentId eq student[Student.studentId])
                }.map {
                    print(it[StudentGrade.grade].toString() + "\n")
                }
            }
        }
        if (args[0] == "u") {
            val courseName = args[1]
            val lastName = args[2]
            val grade = args[3].toInt()
            StudentGrade.insert {
                val student = Student.select {Student.lastName eq lastName}.first()
                val course = Course.select {Course.courseName eq courseName}.first()
                it[studentId] = student[Student.studentId]
                it[courseId] = course[Course.courseId]
                it[StudentGrade.grade] = grade
            }
        }
    }
}