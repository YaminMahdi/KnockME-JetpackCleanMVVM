package com.mlab.knockme.main_feature.presentation

sealed class ProfileInnerScreens(val route:String){
    data object CgpaScreen : ProfileInnerScreens("cgpa_view/")
    data object CgpaInnerScreen : ProfileInnerScreens("cgpa_details_view/")
    data object DueScreen : ProfileInnerScreens("payment_view")  //?fbId={fbId}&pic={pic}
    data object RegCourseScreen : ProfileInnerScreens("reg_view")
    data object LiveResultScreen : ProfileInnerScreens("live_res_view")
    data object ClearanceScreen : ProfileInnerScreens("clearance_view")


}