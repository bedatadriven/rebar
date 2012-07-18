/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package com.bedatadriven.rebar.async;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class NullCallback<T> implements AsyncCallback<T> {

    @Override
    public void onFailure(Throwable throwable) { /* NO OP */ }

    @Override
    public void onSuccess(T t) { /* NO OP */ }
}
