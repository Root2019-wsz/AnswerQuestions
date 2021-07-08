package com.shenzhe.answerquestion.util;

/**
 * 邮问有答的接口地址
 */

public class ApiParam {
    public static final int SUCCESS = 200;

    public static final String REGISTER = "http://xtnb.xyz:8888/register";
    public static final String LOGIN = "http://xtnb.xyz:8888/login";
    public static final String MODIFY_AVATAR = "http://xtnb.xyz:8888/updataAvatar";
    public static final String CHANGE_PASSWORD = "http://xtnb.xyz:8888/updatePassword";
    public static final String ASK_A_QUESTION = "http://xtnb.xyz:8888/addQuestion";
    public static final String GET_QUESTION_LIST = "http://xtnb.xyz:8888/getAllQuestion";
    public static final String ANSWER_A_QUESTION = "http://xtnb.xyz:8888/addAnswer";
    public static final String GET_ANSWER_LIST = "http://xtnb.xyz:8888/getAllAnswerByQuestionId";
    public static final String ADD_FAVORITE = "http://xtnb.xyz:8888/addFavorite";
    public static final String CANCEL_FAVORITE = "http://xtnb.xyz:8888/deleteFavorite";
    public static final String GET_FAVORITE_LIST = "http://xtnb.xyz:8888/getAllFavoriteByUid";
    public static final String ADD_EXCITING_QUESTION = "http://xtnb.xyz:8888/addExcitingQuestion";
    public static final String CANCEL_EXCITING_QUESTION = "http://xtnb.xyz:8888/deleteExcitingQuestionById";
    public static final String IS_EXCITING_QUESTION = "http://xtnb.xyz:8888/isExcitingQuestion";
    public static final String ADD_EXCITING_ANSWER = "http://xtnb.xyz:8888/addExcitingAnswer";
    public static final String DELETE_EXCITING_ANSWER = "http://xtnb.xyz:8888/deleteExcitingAnswerById";
    public static final String IS_EXCITING_ANSWER = "http://xtnb.xyz:8888/isExcitingAnswer";
    public static final String ADD_NATIVE_QUESTION = "http://xtnb.xyz:8888/addNaiveQuestion";
    public static final String DELETE_NATIVE_QUESTION = "http://xtnb.xyz:8888/deleteNaiveQuestionById";
    public static final String IS_NATIVE_QUESTION = "http://xtnb.xyz:8888/isNaiveQuestion";
    public static final String ADD_NATIVE_ANSWER = "http://xtnb.xyz:8888/addNaiveAnswer";
    public static final String DELETE_NATIVE_ANSWER = "http://xtnb.xyz:8888/deleteNaiveAnswerById";
    public static final String IS_NATIVE_ANSWER = "http://xtnb.xyz:8888/isNaiveAnswer";
    public static final String IS_FAVOURITE = "http://xtnb.xyz:8888/isFavorite";
    public static final String GET_TOKEN = "https://portal.qiniu.com/";
    public static final String MY_QINIU_URL = "http://qtywt7ine.hn-bkt.clouddn.com/";//后面拼接文件名

}
