package work.parts.utils.models

data class Part (
    var company_id: String = "",
    var work_id: String = "",
    var part_id: String = "",

    var user_email: String = "",
    var user_name: String = "",

    var data_time: String = "",
    var working_time: Int = 0,
    var work_done: String = "",
    var comment: String = "",
    var km: Int = 0,
    var costs: Int = 0,
    
    var ml: String = ""
        )