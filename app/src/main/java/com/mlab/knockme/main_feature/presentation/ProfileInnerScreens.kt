package com.mlab.knockme.main_feature.presentation

sealed class ProfileInnerScreens(val route:String){
    object CgpaScreen : ProfileInnerScreens("cgpa_view/")
    object CgpaInnerScreen : ProfileInnerScreens("cgpa_details_view/")
    object DueScreen : ProfileInnerScreens("payment_view")  //?fbId={fbId}&pic={pic}
    object RegCourseScreen : ProfileInnerScreens("reg_view")
    object LiveResultScreen : ProfileInnerScreens("live_res_view")

}