/*************************
 * news.thrift
 * Lesports Bole
 *************************************/
namespace java com.lesports.bole.api.vo

struct TBParagraph {
    1: optional string content;
    2: optional i16 order;
}

struct TBImage {
    1: optional string url;
    2: optional i16 order;
    3: optional string name;
}

/**
* 新闻信息
**/
struct  TBNews {
    // id
    1: i64 id,
    //新闻标题
    2: optional string title,
    //发布时间
    3: optional string publishAt,
    //段落列表
    4: optional list<TBParagraph> paragraphs,
    //状态
    5: optional list<TBImage> images;
    //评论数
    6: optional i32 commentCount;
    //标签
    7: optional set<string> tags;
}
