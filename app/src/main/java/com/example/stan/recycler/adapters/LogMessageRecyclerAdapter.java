/*
 * Copyright <2017> <Stanislas Daniel Claude Dolcini>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.example.stan.recycler.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.stan.recycler.R;
import com.example.stan.recycler.model.LogMessage;
import com.example.stan.recycler.viewholders.LogMessageViewHolder;

import java.util.List;

/**
 * Created by Stanislas Daniel Claude Dolcini on 02/02/17.
 */
public class LogMessageRecyclerAdapter extends RecyclerView.Adapter<LogMessageViewHolder> {
    private final List<LogMessage> logMessages;

    public LogMessageRecyclerAdapter(List<LogMessage> logMessages) {
        this.logMessages = logMessages;
    }

    @Override
    public LogMessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.studentlayout,parent,false);
        return new LogMessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LogMessageViewHolder holder, int position) {
        LogMessage logMessage = logMessages.get(position);
        holder.bind(logMessage);
    }

    @Override
    public int getItemCount() {
        return logMessages == null ? 0 : logMessages.size();
    }
}
