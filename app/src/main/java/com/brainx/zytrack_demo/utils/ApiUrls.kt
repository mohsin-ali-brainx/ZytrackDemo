package com.brainx.zytrack_demo.utils

object ApiUrls {
    private const val VERSION = "v1/"
    private const val API = "api/"
    private const val USERS = "users/"
    private const val USER = "user/"
    private const val TIMESHEETS = "timesheets"

    private const val USERS_API_SUB_ROOT = "${API}${VERSION}${USERS}" ///api/v1/users/
    private const val USER_API_SUB_ROOT = "${API}${VERSION}${USER}" ///api/v1/user/
    private const val TIMESHEET_SUB_ROOT = "${API}${VERSION}${TIMESHEETS}" ///api/v1/timesheets/
    const val SIGN_IN = "/${USERS_API_SUB_ROOT}sign_in.json"///api/v1/users/sign_in.json
    const val FORGOT_PASSWORD = "/${USERS_API_SUB_ROOT}password.json" ///api/v1/users/password.json
    const val LOG_OUT = "/${USERS_API_SUB_ROOT}sign_out.json" ///api/v1/users/sign_out.json
    const val ADD_TIMESHEET = "/${TIMESHEET_SUB_ROOT}/add_timesheet.json" ///api/v1/timesheets/add_timesheet.json
    const val DATE_TIMESHEET = "/${TIMESHEET_SUB_ROOT}" ///api/v1/timesheets
    const val EDIT_TIMESHEET = "/${TIMESHEET_SUB_ROOT}/{id}"
    const val DELETE_TIMESHEET = "/${TIMESHEET_SUB_ROOT}/{id}"
    const val PROFILE = "/${USER_API_SUB_ROOT}profile.json"
    const val PROJECTS = "${API}${VERSION}projects"
}