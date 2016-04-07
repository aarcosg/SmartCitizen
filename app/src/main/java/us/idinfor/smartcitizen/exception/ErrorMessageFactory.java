package us.idinfor.smartcitizen.exception;

import android.content.Context;

import us.idinfor.smartcitizen.R;
import us.idinfor.smartcitizen.data.api.hermes.exception.UserAlreadyExistsErrorHermesException;
import us.idinfor.smartcitizen.data.api.hermes.exception.UserNotRegisteredErrorHermesException;

public class ErrorMessageFactory {

    private ErrorMessageFactory() {}

    public static String create(Context context, Throwable throwable) {
        String message = context.getString(R.string.exception_message_generic);

        if (throwable instanceof UserAlreadyExistsErrorHermesException) {
            message = context.getString(R.string.exception_message_user_already_taken);
        } else if (throwable instanceof UserNotRegisteredErrorHermesException) {
            message = context.getString(R.string.exception_message_user_not_found);
        }

        return message;
    }
}