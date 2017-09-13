package es.us.hermes.smartcitizen.exception;

import android.content.Context;

import es.us.hermes.smartcitizen.R;
import es.us.hermes.smartcitizen.exception.google.fit.QueryDataGoogleFitException;
import es.us.hermes.smartcitizen.exception.google.fit.QueryTimeOutGoogleFitException;
import es.us.hermes.smartcitizen.exception.hermes.UserAlreadyExistsHermesException;
import es.us.hermes.smartcitizen.exception.hermes.UserNotRegisteredHermesException;

public class ErrorMessageFactory {

    private ErrorMessageFactory() {}

    public static String create(Context context, Throwable throwable) {
        String message = context.getString(R.string.exception_message_generic);

        if (throwable instanceof UserAlreadyExistsHermesException) {
            message = context.getString(R.string.exception_message_user_already_taken);
        } else if (throwable instanceof UserNotRegisteredHermesException) {
            message = context.getString(R.string.exception_message_user_not_found);
        } else if (throwable instanceof InternetNotAvailableException) {
            message = context.getString(R.string.exception_message_no_connection);
        } else if (throwable instanceof PermissionRequiredException) {
            message = context.getString(R.string.exception_message_permissions_denied);
        } else if (throwable instanceof QueryDataGoogleFitException || throwable instanceof QueryTimeOutGoogleFitException) {
            message = context.getString(R.string.exception_message_google_fit_query);
        }

        return message;
    }
}