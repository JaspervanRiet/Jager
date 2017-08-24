/*
 * Copyright (C) 2015 Jasper van Riet
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.jaspervanriet.huntingthatproduct.Data.Http;

import android.os.AsyncTask;

import java.net.HttpURLConnection;

import retrofit.RetrofitError;
import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class RetryWithSessionRefresh implements
		Func1<Observable<? extends Throwable>, Observable<?>> {

	private final SessionService sessionSerivce;
	private final AccessTokenProvider accessTokenProvider;

	public RetryWithSessionRefresh (SessionService sessionSerivce,
	                                AccessTokenProvider accessTokenProvider) {
		this.sessionSerivce = sessionSerivce;
		this.accessTokenProvider = accessTokenProvider;
	}

	@Override
	public Observable<?> call (Observable<? extends Throwable> observable) {
		return observable
				.flatMap (new Func1<Throwable, Observable<?>> () {
					int retryCount = 0;

					@Override
					public Observable<?> call (Throwable throwable) {
						retryCount++;
						// Retry once
						if (retryCount <= 1 && throwable instanceof RetrofitError) {
							final RetrofitError retrofitError = (RetrofitError) throwable;
							if (!retrofitError.getKind ().equals (RetrofitError.Kind.NETWORK)
									&& retrofitError.getResponse ().getStatus () ==
									HttpURLConnection.HTTP_UNAUTHORIZED) {
								return sessionSerivce
										.askForToken ()
										.doOnNext (accessTokenProvider::setSessionToken)
										.doOnError (throwable1 -> accessTokenProvider.resetSessionToken ())
										.observeOn (Schedulers.from (AsyncTask.THREAD_POOL_EXECUTOR))
										.subscribeOn (Schedulers.from (AsyncTask.THREAD_POOL_EXECUTOR));
							}
						}
						// No more retries, pass through error
						return Observable.error (throwable);
					}
				});
	}
}
