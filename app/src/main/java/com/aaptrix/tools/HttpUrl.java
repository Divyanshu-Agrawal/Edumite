package com.aaptrix.tools;

import java.io.Serializable;

import static com.aaptrix.activitys.SplashScreen.HTTP_HOST;

public class HttpUrl implements Serializable {

    private static long tokenvalue = System.currentTimeMillis();

    public static String TC = "https://dashboard.educoachapp.com/policies/terms.html";
    public static String PV = "https://dashboard.educoachapp.com/policies/privacy-policy.html";

    public static final String ALL_STATES = HTTP_HOST + "all_states.php?tokenvalue=" + tokenvalue;
    public static final String ALL_CITY = HTTP_HOST + "city_list.php?tokenvalue=" + tokenvalue;
    public static final String ALL_SCHOOL = HTTP_HOST + "school_list.php?tokenvalue=" + tokenvalue;
    public static final String ALL_ROLE = HTTP_HOST + "role_list.php?tokenvalue=" + tokenvalue;
    public static final String LOGIN_URL = HTTP_HOST + "login/locklogin.php?tokenvalue=" + tokenvalue;
    public static final String VERIFY_PHONE = HTTP_HOST + "login/user_mobile_verification.php";
    public static final String REGISTER_URL = HTTP_HOST + "login/user_register.php?tokenvalue=" + tokenvalue;
    public static final String ALL_INSTITUTE_BUZZ_CATE = HTTP_HOST + "all_insti_buzz_cate.php?tokenvalue=" + tokenvalue;
    public static final String UPDATE_OTP = HTTP_HOST + "login/user_update_otp.php?tokenvalue=" + tokenvalue;
    public static final String UPDATE_PHONE_NUMBER = HTTP_HOST + "login/user_update_phone_number.php?tokenvalue=" + tokenvalue;
    public static final String UPDATE_USER_PRO_IMAGE = HTTP_HOST + "update_user_pro_image.php?tokenvalue=" + tokenvalue;
    public static final String UPDATE_USER_PRO_PASSWORD = HTTP_HOST + "login/update_user_pro_password.php?tokenvalue=" + tokenvalue;
    public static final String CHANGE_PASSWORD = HTTP_HOST + "login/change_user_password.php?tokenvalue=" + tokenvalue;
    public static final String FORGOT_PASSWORD = HTTP_HOST + "login/forgot_password.php?tokenvalue=" + tokenvalue;
    public static final String ABOUT_SCHOOL_INFO = HTTP_HOST + "about_school_details.php?tokenvalue=" + tokenvalue;
    public static final String ALL_EVENTS = HTTP_HOST + "all_school_events.php?tokenvalue=" + tokenvalue;
    public static final String ALL_UPCOMMING_EVENTS_UNIQUE = HTTP_HOST + "upcomming_all_school_events_unique.php?tokenvalue=" + tokenvalue;
    public static final String ALL_ACTIVITIES = HTTP_HOST + "all_school_activities.php?tokenvalue=" + tokenvalue;
    public static final String ADD_ACTIVITY = HTTP_HOST + "user_add_activity.php?tokenvalue=" + tokenvalue;
    public static final String UPDATE_ACTIVITY = HTTP_HOST + "user_update_activity.php?tokenvalue=" + tokenvalue;
    public static final String REMOVE_ACTIVITY = HTTP_HOST + "user_remove_activity.php?tokenvalue=" + tokenvalue;
    public static final String ADD_EVENT = HTTP_HOST + "user_add_event.php?tokenvalue=" + tokenvalue;
    public static final String ALL_PUBLICATION = HTTP_HOST + "all_school_publication.php?tokenvalue=" + tokenvalue;
    public static final String ADD_PUBLICATION = HTTP_HOST + "user_add_publication.php?tokenvalue=" + tokenvalue;
    public static final String UPDATE_PUBLICATION = HTTP_HOST + "user_update_publication.php?tokenvalue=" + tokenvalue;
    public static final String REMOVE_PUBLICATION = HTTP_HOST + "user_remove_publication.php?tokenvalue=" + tokenvalue;
    public static final String ALL_ATTENDANCE = HTTP_HOST + "list_of_attendance.php?tokenvalue=" + tokenvalue;
    public static final String ALL_BATCHS = HTTP_HOST + "list_of_batches.php?tokenvalue=" + tokenvalue;
    public static final String ALL_STUDENTS = HTTP_HOST + "list_of_students.php?tokenvalue=" + tokenvalue;
    public static final String ALL_STUDENTS_VIEW = HTTP_HOST + "list_of_students_view.php?tokenvalue=" + tokenvalue;
    public static final String SUBMIT_ATTENDANCE = HTTP_HOST + "submit_attendance.php?tokenvalue=" + tokenvalue;
    public static final String ALL_EXAMS = HTTP_HOST + "list_of_exams.php?tokenvalue=" + tokenvalue;
    public static final String ALL_EXAMS_NEXT = HTTP_HOST + "list_of_next_exam.php?tokenvalue=" + tokenvalue;
    public static final String LIST_OF_CLASS_TIME_TABLE = HTTP_HOST + "list_of_class_time_table.php?tokenvalue=" + tokenvalue;
    public static final String LIST_OF_DAIRYS = HTTP_HOST + "list_of_dairy.php?tokenvalue=" + tokenvalue;
    public static final String LIST_OF_HOMEWORK = HTTP_HOST + "list_of_homework.php?tokenvalue=" + tokenvalue;
    public static final String TEACHER_ADD_DAIRY = HTTP_HOST + "teacher_add_dairy.php?tokenvalue=" + tokenvalue;
    public static final String TEACHER_UPDATE_DAIRY = HTTP_HOST + "user_update_dairy.php?tokenvalue=" + tokenvalue;
    public static final String TEACHER_ADD_HOMEWORK = HTTP_HOST + "teacher_add_homework.php?tokenvalue=" + tokenvalue;
    public static final String TEACHER_UPDATE_HOMEWORK = HTTP_HOST + "user_update_homework.php?tokenvalue=" + tokenvalue;
    public static final String LIST_OF_SUBJECTS = HTTP_HOST + "list_of_subjects.php?tokenvalue=" + tokenvalue;
    public static final String SWITCH_USERS = HTTP_HOST + "login/switch_user.php?tokenvalue=" + tokenvalue;
    public static final String ALL_STUDENTS_LIST = HTTP_HOST + "all_students.php?tokenvalue=" + tokenvalue;
    public static final String USER_RESULT = HTTP_HOST + "result_list.php?tokenvalue=" + tokenvalue;
    public static final String ALL_PREVIOUS_RESULT = HTTP_HOST + "result_list_prev.php?tokenvalue=" + tokenvalue;
    public static final String ALL_NEXT_RESULT = HTTP_HOST + "result_list_next.php?tokenvalue=" + tokenvalue;
    public static final String ALL_EXAM_LIST = HTTP_HOST + "all_exam_list.php?tokenvalue=" + tokenvalue;
    public static final String ALL_SUBJECT_LIST = HTTP_HOST + "all_subject_list_in_exam.php?tokenvalue=" + tokenvalue;
    public static final String ALL_STUDENTS_FOR_RESULT = HTTP_HOST + "list_of_students_for_result.php?tokenvalue=" + tokenvalue;
    public static final String ALL_STUDENTS_FOR_VIEW_RESULT = HTTP_HOST + "list_of_students_for_view_result.php?tokenvalue=" + tokenvalue;
    public static final String SUBMIT_RESULT = HTTP_HOST + "submit_result.php?tokenvalue=" + tokenvalue;
    public static final String SAVE_RESULT = HTTP_HOST + "save_result.php?tokenvalue=" + tokenvalue;
    public static final String SEND_MESSAGE = HTTP_HOST + "send_message.php?tokenvalue=" + tokenvalue;
    public static final String SUBMIT_LEAVE = HTTP_HOST + "submit_leave.php?tokenvalue=" + tokenvalue;
    public static final String SAVED_RESULT_LIST = HTTP_HOST + "saved_result_student_list.php?tokenvalue=" + tokenvalue;
    public static final String ALL_LEAVES = HTTP_HOST + "list_of_leaves.php?tokenvalue=" + tokenvalue;
    public static final String ALL_FILTER_LEAVES = HTTP_HOST + "list_of_filter_leaves.php?tokenvalue=" + tokenvalue;
    public static final String ALL_STUDENT_LEAVES = HTTP_HOST + "list_of_student_leaves.php?tokenvalue=" + tokenvalue;
    public static final String SUBMIT_STATUS = HTTP_HOST + "update_leave_status.php?tokenvalue=" + tokenvalue;
    public static final String RESULT_CHECK = HTTP_HOST + "result_check.php?tokenvalue=" + tokenvalue;
    public static final String UPDAATE_EXAM_TT = HTTP_HOST + "update_exam_time_table.php?tokenvalue=" + tokenvalue;
    public static final String ALL_GALLERY = HTTP_HOST + "all_school_gallery.php?tokenvalue=" + tokenvalue;
    public static final String ADD_GALLERY = HTTP_HOST + "user_add_gallery.php?tokenvalue=" + tokenvalue;
    public static final String REMOVE_GALLERY = HTTP_HOST + "user_remove_gallery.php?tokenvalue=" + tokenvalue;
    public static final String ALL_VIDEOS = HTTP_HOST + "study_video_material/all_school_studyVideos.php?tokenvalue=" + tokenvalue;
    public static final String ADD_VIDEOS = HTTP_HOST + "study_video_material/user_add_studyVideos.php?tokenvalue=" + tokenvalue;
    public static final String REMOVE_VIDEOS = HTTP_HOST + "study_video_material/user_remove_studyVideos.php?tokenvalue=" + tokenvalue;
    public static final String ALL_STUDY_MATERIAL = HTTP_HOST + "study_video_material/all_school_studyMaterial.php?tokenvalue=" + tokenvalue;
    public static final String ADD_STUDY_MATERIAL = HTTP_HOST + "study_video_material/user_add_studyMaterial.php?tokenvalue=" + tokenvalue;
    public static final String REMOVE_STUDY_MATERIAL = HTTP_HOST + "study_video_material/user_remove_studyMaterial.php?tokenvalue=" + tokenvalue;
    public static final String STAFF_LIST = HTTP_HOST + "users_attendance/list_of_users.php?tokenvalue=" + tokenvalue;
    public static final String STAFF_VIEW_ATTENDANCE = HTTP_HOST + "users_attendance/list_of_attendance_view.php?tokenvalue=" + tokenvalue;
    public static final String STAFF_SUBMIT_ATTENDANCE = HTTP_HOST + "users_attendance/submit_attendance.php?tokenvalue=" + tokenvalue;
    public static final String STAFF_OWN_ATTENDANCE = HTTP_HOST + "users_attendance/list_of_attendance.php?tokenvalue=" + tokenvalue;
    public static final String STAFF_LEAVE_APPLICATION = HTTP_HOST + "users_attendance/submit_leave.php?tokenvalue=" + tokenvalue;
    public static final String STAFF_LEAVE_STATUS = HTTP_HOST + "users_attendance/update_leave_status.php?tokenvalue=" + tokenvalue;
    public static final String ADMIN_ALL_LEAVES = HTTP_HOST + "users_attendance/list_of_leaves.php?tokenvalue=" + tokenvalue;
    public static final String ADMIN_FILTER_LEAVES = HTTP_HOST + "users_attendance/list_of_filter_leaves.php?tokenvalue=" + tokenvalue;
    public static final String STAFF_OWN_LEAVES = HTTP_HOST + "users_attendance/list_of_users_leaves.php?tokenvalue=" + tokenvalue;
    public static final String STUDENT_RESULT = HTTP_HOST + "student_all_results.php?tokenvalue=" + tokenvalue;
    public static final String MIN_MAX_MARKS = HTTP_HOST + "student_results_min_max.php?tokenvalue=" + tokenvalue;
    public static final String ADMIN_CONTACTS = HTTP_HOST + "all_contacts_list_for_admin.php?tokenvalue=" + tokenvalue;
    public static final String REFER_DATA = HTTP_HOST + "referral_code_offer.php?tokenvalue=" + tokenvalue;
    public static final String GET_SUBJECT = HTTP_HOST + "getSubjectByBatchId.php?tokenvalue=" + tokenvalue;
    public static final String STAFF_DETAILS = HTTP_HOST + "staff/staff_details_by_batch.php?tokenvalue=" + tokenvalue;
    public static final String SEND_FEEDBACK = HTTP_HOST + "feedback/add_feedback.php?tokenvalue=" + tokenvalue;
    public static final String SEND_RATING = HTTP_HOST + "staff/add_staff_rating.php?tokenvalue=" + tokenvalue;
    public static final String MINOR_RESULT = HTTP_HOST + "student_all_results_minor.php?tokenvalue=" + tokenvalue;
    public static final String REMOVE_HW = HTTP_HOST + "teacher_remove_homework.php?tokenvalue=" + tokenvalue;
    public static final String REMOVE_DIARY = HTTP_HOST + "teacher_remove_diary.php?tokenvalue=" + tokenvalue;
    public static final String REMOVE_EVENT = HTTP_HOST + "admin_remove_event.php?tokenvalue=" + tokenvalue;
    public static final String GET_SUBS = HTTP_HOST + "study_video_material/getSubjectByMultiBatchId.php?tokenvalue=" + tokenvalue;
    public static final String ADD_LEAD = HTTP_HOST + "enquiry/user_add_enquiry.php?tokenvlaue=" + tokenvalue;
    public static final String VIEW_ENQUIRY = HTTP_HOST + "enquiry/list_of_enquiry.php?tokenvalue=" + tokenvalue;
    public static final String ADD_EXAM_TIMETABLE = HTTP_HOST + "exam/addexam.php?tokenvalue=" + tokenvalue;
    public static final String UPDATE_CLASS_TT = HTTP_HOST + "user_update_class_tt.php?tokenvalue=" + tokenvalue;
    public static final String GET_COURSES = HTTP_HOST + "list_of_course.php?tokenvalue=" + tokenvalue;
    public static final String LOGOUT = HTTP_HOST + "logout.php?tokenvalue=" + tokenvalue;
    public static final String GET_PERMISSION = HTTP_HOST + "all_permissions.php?tokenvalue=" + tokenvalue;
    public static final String ALL_ONLINE_EXAM = HTTP_HOST + "onlineExam/all_exam_list.php?tokenvalue=" + tokenvalue;
    public static final String ONLINE_EXAM_QUES = HTTP_HOST + "onlineExam/question_details_by_exam_id.php?tokenvalue=" + tokenvalue;
    public static final String SUBMIT_ONLINE_EXAM = HTTP_HOST + "onlineExam/submit_online_exam.php?tokenvalue=" + tokenvalue;
    public static final String USER_REGISTRATION = HTTP_HOST + "add_student_from_app.php?tokenvalue=" + tokenvalue;
    public static final String HELP = HTTP_HOST + "help.php?tokenvalue=" + tokenvalue;
    public static final String STUDENT_FEE_DETAIL = HTTP_HOST + "fees/student_fees_details.php?tokenvalue=" + tokenvalue;
    public static final String SUBMIT_STUDENT_FEES = HTTP_HOST + "fees/add_student_fees_upi.php?tokenvalue=" + tokenvalue;
    public static final String ADD_LIVE_STREAM = HTTP_HOST + "live_streaming/user_add_liveStreamingVideos.php?tokenvalue=" + tokenvalue;
    public static final String GET_COMMENTS = HTTP_HOST + "live_streaming/list_of_live_streaming_comment.php?tokenvalue=" + tokenvalue;
    public static final String GET_LIVE_STREAM = HTTP_HOST + "live_streaming/all_liveStreamingVideos.php?tokenvalue=" + tokenvalue;
    public static final String ADD_COMMENTS = HTTP_HOST + "live_streaming/add_live_streaming_comment.php?tokenvalue=" + tokenvalue;
    public static final String REMOVE_LIVE = HTTP_HOST + "live_streaming/live_streaming_status_change.php?tokenvalue=" + tokenvalue;
    public static final String BATCH_BY_COURSE = HTTP_HOST + "list_of_batches_by_courseid.php?tokenvalue=" + tokenvalue;
    public static final String GUEST_EXAM = HTTP_HOST + "onlineExamGuest/all_exam_list.php?tokenvalue=" + tokenvalue;
    public static final String GUEST_EXAM_DETAILS = HTTP_HOST + "onlineExamGuest/question_details_by_exam_id.php?tokenvalue=" + tokenvalue;
    public static final String LEAD_GENERATION = HTTP_HOST + "onlineExamGuest/submit_online_exam.php?tokenvalue=" + tokenvalue;
    public static final String SUBMIT_SUBJECTIVE_EXAM = HTTP_HOST + "subjectiveOnlineExam/submit_subjective_online_exam.php?tokenvalue=" + tokenvalue;
    public static final String SUBJECTIVE_RESULT = HTTP_HOST + "subjectiveOnlineExam/result_subjective_exam.php?tokenvalue=" + tokenvalue;
    public static final String UPDATE_PROFILE = HTTP_HOST + "update_user_details.php?tokenvalue=" + tokenvalue;
    public static final String TESTIMONIALS = HTTP_HOST + "feedback/all_feedback_by_school.php?tokenvalue=" + tokenvalue;
    public static final String STAFF_RATING = HTTP_HOST + "staff/all_comments_rating_by_staff_id.php?tokenvalue=" + tokenvalue;
    public static final String STUDENT_VIDEO = HTTP_HOST + "study_video_material/all_school_studyVideos_limit10.php?tokenvalue=" + tokenvalue;
}