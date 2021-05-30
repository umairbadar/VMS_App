package com.fyp.vmsapp.utilities;

import android.Manifest;
import java.util.concurrent.TimeUnit;

abstract public class Constants {
    static String[] requiredPermissions = new String[] {
            Manifest.permission.INTERNET,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.VIBRATE,
            Manifest.permission.RECORD_AUDIO
    };

    static public String BaseURL = "https://vaccinationmanagementsystem.com",
            EndpointPrefix = "/public/api/";

    static public String

            EndpointLogin = "user/login",
            EndpointSignup = "user/signup",
            EndpointGetData = "data",
            EndpointAddMember = "family_member/submit",
            EndpointList = "family_member/list",
            EndpointDeleteMember = "family_member/delete",
            EndpointGetVaccination = "get_vaccination",
            EndpointNearbyHospitals = "hospitals",
            EndpointArticles = "get_articles_title",
            EndpointArticleContent = "get_articles_content",
            EndpointInjectVaccination = "vaccination_injected",
            EndpointUploadVaccinationSlip = "vaccination_upload_slip",
            EndpointGetProfile = "get_profile",
            EndpointUpdateProfile = "update_profile",

    StaffRole = "staff",  //last mile
            RiderRole = "rider",  //last mile

    RemainingSeparator = " out of ",

    NavigatePrefix = "http://maps.google.com/maps?daddr=",
            CallPrefix = "tel:",
            SupportContactNumber ="+923102923498",
            ImageUrl = BaseURL + "/storage/";

    static int RequestTimeoutDuration = 1;

    static public int RequestCode = 101,

    MethodGET = 0,
            MethodPOSTSimple = 1,
            MethodPOSTMultipart = 2,

    GetPickupsRepeatInterval = 5,
            SendPickupActionLogsRepeatInterval = 30000,
            SendPickupsRepeatInterval = 30000,
    // Last Mile
    GetDeliveryRepeatInterval = 5,
            GetDNCCRepeatInterval = 5,
            GetRiderProfileRepeatInterval = 5,
            GetSignupDetailsRepeatInterval = 5,
            SendSignupDataRepeatInterval = 5,
            GetSliderImagesRepeatInterval = 5,
            SendDeliveryActionLogsRepeatInterval = 30000,
            SendDeliveryRepeatInterval = 30000,
            SendRiderCommentRepeatInterval= 30000,

    GetReturnRepeatInterval = 5,
            SendReturnActionLogsRepeatInterval = 30000,
            SendReturnRepeatInterval = 30000,

    RefreshInterval = 60000, //1 Minute
            PullToRefreshInterval = 5000, //5 seconds

            LocationInterval = 10000, //10 Seconds
            LocationFastestInterval = 5000, //5 Seconds
            LocationMaxWaitTime = 300000, //5 Minutes

    PickupIDDefault = 0,
            DeliveryIDDefault = 0, ShipmentIdDefault = 0, ReturnIDDefault = 0,

    PickupActionTypeNavigate = 1,
            PickupActionTypeCall = 2,
            PickupActionTypeNotPick = 3,
            PickupActionTypePick = 4,

    DeliveryActionTypeNavigate = 1,
            DeliveryActionTypeCall = 2,
            DeliveryActionTypeUndelivered = 3,
            DeliveryActionTypeDelivered = 4,
            DeliveryActionTypeRequestDetails = 5,
            DeliveryActionTypeContactSupport = 6,

    PickupTypeNotPick = 0,
            PickupTypePick = 1,
            PickupStatusPending = 0,
            PickupStatusPicked = 1,
            PickupStatusNotPicked = 2,

    DeliveryTypeUndelivered = 1,
            DeliveryTypeDelivered = 2,
            DeliveryStatusPending = 1,
            DeliveryStatusDelivered = 2,
            DeliveryStatusUndelivered = 3;

    static TimeUnit RequestTimeoutTimeUnit = TimeUnit.MINUTES;

    static public TimeUnit GetPickupsRepeatIntervalTimeUnit = TimeUnit.MINUTES,
            SendPickupActionLogsRepeatIntervalTimeUnit = TimeUnit.SECONDS,
            SendPickupsRepeatIntervalTimeUnit = TimeUnit.SECONDS,
            GetDeliveryRepeatIntervalTimeUnit = TimeUnit.MINUTES,  //last mile
            GetReturnRepeatIntervalTimeUnit = TimeUnit.MINUTES,  //last mile
            SendDeliveryActionLogsRepeatIntervalTimeUnit = TimeUnit.MINUTES, //last mile
            SendDeliveryRepeatIntervalTimeUnit = TimeUnit.SECONDS, //last mile
            SendRiderCommentsRepeatIntervalTimeUnit = TimeUnit.SECONDS, //last mile
            GetDNCCIntervalTimeUnit = TimeUnit.MINUTES,
            GetRiderProfileIntervalTimeUnit = TimeUnit.MINUTES,
            GetSignupDetailsIntervalTimeUnit = TimeUnit.MINUTES,
            SendSignupDataIntervalTimeUnit = TimeUnit.MINUTES,
            GetSliderImagesIntervalTimeUnit = TimeUnit.MINUTES;

    static public String[] PickupNotPickReasons = {"Select Reason", "Address Closed",
            "Address Incomplete", "Incorrect Location", "Shipments are not Ready",
            "Contact Person Unavailable", "To be Picked Later", "Not Attempted",
            "Late Attempted", "Accident/Snatching", "Refused on Call"};

    //last mile
    static public String[] DeliveryNotDeliverReasons = {"Select Reason", "Address Closed",
            "Address Incomplete", "Incorrect Location", "Shipments are not Ready",
            "Contact Person Unavailable", "To be Picked Later", "Not Attempted",
            "Late Attempted", "Accident/Snatching"};

    static public Double DefaultCoordinates = 0.0;



}
