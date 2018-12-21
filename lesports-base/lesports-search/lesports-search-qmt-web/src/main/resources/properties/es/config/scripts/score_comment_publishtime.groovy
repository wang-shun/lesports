publish_time = doc['publishTime'].value;
comment_count = doc['commentCount'].value;
divisor = 45000.00;
time_score = (publish_time - 20160101000000) / divisor;
comment_score = log10(comment_count);
return (time_score + comment_score) * _score;