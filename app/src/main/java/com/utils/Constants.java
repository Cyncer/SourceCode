package com.utils;

/**
 * Created by ketul.patel on 21/1/16.
 */
public interface Constants {


    String liveURL = "http://cync.com.au/cyncapp";
    String baseDomain = "http://opensource.zealousys.com:81/cync";

//    String imagBaseUrl = "http://opensource.zealousys.com:81/cync/wp-content/uploads/";
//    String baseUrl = baseDomain + "/wp-content/themes/cync/webservices";
    String imagBaseUrl = "http://cync.com.au/cyncapp/wp-content/uploads/";
    String baseUrl = liveURL+"/wp-content/themes/cync/webservices";


    String loginUrl = baseUrl + "/login.php?";
    String registerUrl = baseUrl + "/registration.php?";
    String forgotpasswordUrl = baseUrl + "/forgot_password.php?";
    String changePassword = baseUrl + "/change_password.php?";
    String shareLocationUrl = baseUrl + "/share_location.php?";
    // Friend
    String getuserdetails = baseUrl + "/getuserdetails.php?";
    String updateProfileUrl = baseUrl + "/update_profile.php?";
    String get_friend_list = baseUrl + "/get_friend_list.php?";
    String deleteFriends = baseUrl + "/user_friend_request.php?";
    String clear_Notification = baseUrl + "/clear_notifications.php?";
    String sendFriendRequest = baseUrl + "/user_friend_request.php?";
    String inviteFriend = baseUrl + "/invite_friend.php?";
    // Group
    String create_group = baseUrl + "/create_group.php?";
    String get_group_list = baseUrl + "/get_group_list.php?";
    String getGroupInfo = baseUrl + "/get_group_info.php?";
    String updateGroupInfo = baseUrl + "/update_group.php?";
    String remove_group = baseUrl + "/remove_group.php?";
    String gernalNotification = baseUrl + "/get_all_notifications.php?";
    String getAllUsers = baseUrl + "/get_users.php?";
    String logout = baseUrl + "/logout.php?";
    String delete_group_member = baseUrl + "/delete_group_member.php?";
    String getGroupFrienList = baseUrl + "/get_group_friends.php?";
    String add_group_member = baseUrl + "/add_group_member.php?";
    String groupLocationUpdate = baseUrl + "/share_location_group.php?";
    String responceGroupRequest = baseUrl + "/accept_group_request.php?";
    String termcondition = baseUrl + "/term-condition.php?";
    String getAllNotification = baseUrl + "/get_general_notification.php?";
    String notification_setting = baseUrl + "/notification_setting.php?";
    String getMake = baseUrl + "/car_make.php";
    String getYear = baseUrl + "/car_year.php?make_id=";
    String getModel = baseUrl + "/get_car_model.php?make_id=";
    String updateTime = baseUrl + "/hour_calculate.php?";
    String assistUrl = baseUrl + "/get_assist_details.php";
    String assistDetailUrl = baseUrl + "/mechinic_info.php?mechenic_id=";
    String create_postUrl = baseUrl + "/create_post.php";
    String update_postUrl = baseUrl + "/update_post.php";
    String getpostUrl = baseUrl + "/posts.php";
    String delete_postUrl = baseUrl + "/delete_post.php";
    String report_postUrl = baseUrl + "/report_post.php";
    String block_userUrl = baseUrl + "/block_user.php";
    String blocked_friendsListUrl = baseUrl + "/blocked_friends.php";
    String like_commentUrl = baseUrl + "/like_comment.php";
    String addcommentUrl = baseUrl + "/add_commentlike.php";
    String deletecommentUrl = baseUrl + "/delete_comment.php";
    String report_commentUrl = baseUrl + "/report_comment.php";
    String edit_commentUrl = baseUrl + "/edit_comment.php";
    String post_detailUrl = baseUrl + "/post_detail.php";
    String update_settingUrl = baseUrl + "/update_setting.php";
    String get_settingUrl = baseUrl + "/get_setting.php";
    String create_rideUrl = baseUrl + "/create_ride.php";
    //  String  get_rideUrl =  baseUrl +"/get_ride.php" ;
    String get_rideUrl = baseUrl + "/get_ride_ids.php";
    String get_myrideUrl = baseUrl + "/get_myride.php";
    String delete_rideUrl = baseUrl + "/delete_ride.php";

//    public static String domain = "@wampserver";
//    public static final String HOST = "demo.zealousys.com";
  public static String domain = "@s132-148-17-112.secureserver.net";
  public static final String HOST = "132.148.17.112";



}