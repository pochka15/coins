rootProject.name = "coins"
include("backend")
include("backend:jooq-generator")
findProject(":backend:jooq-generator")?.name = "jooq-generator"
