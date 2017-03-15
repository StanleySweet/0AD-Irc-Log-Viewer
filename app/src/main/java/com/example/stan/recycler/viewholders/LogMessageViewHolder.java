/*
 * Copyright <2017> <Stanislas Daniel Claude Dolcini>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.example.stan.recycler.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.stan.recycler.model.LogMessage;
import com.example.stan.recycler.R;

/**
 * Created by Stanislas Daniel Claude Dolcini on 02/02/17.
 */
public class LogMessageViewHolder extends RecyclerView.ViewHolder {

    private TextView nickName,timeStamp,textMessage;


    public LogMessageViewHolder(View itemView) {
        super(itemView);
        nickName= (TextView) itemView.findViewById(R.id.nickName);
        textMessage = (TextView) itemView.findViewById(R.id.textMessage);
        timeStamp = (TextView) itemView.findViewById(R.id.timeStamp);
    }


    public void bind(LogMessage logMessage) {
        nickName.setText(logMessage.getNickName());
        textMessage.setText(logMessage.getTextMessage());
        timeStamp.setText(logMessage.getTimeStamp());
    }
}
