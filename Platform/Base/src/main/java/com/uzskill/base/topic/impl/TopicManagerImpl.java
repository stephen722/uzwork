package com.uzskill.base.topic.impl;

import java.util.List;

import com.uzskill.base.manager.BaseManager;
import com.uzskill.base.topic.TopicManager;
import com.uzskill.base.topic.TopicQuery;
import com.uzskill.base.topic.bean.Topic;
import com.uzskill.base.topic.bean.TopicComment;
import com.uzskill.base.topic.bean.TopicPraise;

public class TopicManagerImpl extends BaseManager implements TopicManager {
	
	@Override
	public Topic getTopic(int topicId) {
		Topic topic = getRedis().hGet(TopicQuery.REDIS_TOPIC_MAP, String.valueOf(topicId), Topic.class);
		if(topic == null || topic.getTopicId() <= 0) {
			topic = select(TopicQuery.GET_TOPIC_BY_ID, topicId);
			if(topic != null && topic.getTopicId() > 0) {
				getRedis().hSet(TopicQuery.REDIS_TOPIC_MAP, String.valueOf(topicId), Topic.class);
			}
		}
		return topic;
	}

	@Override
	public List<Topic> getTopTopics() {
		return getTopicList(true, 0);
	}

	@Override
	public List<Topic> getNextTopics(int start) {
		return getTopicList(false, start);
	}

	/**
	 * Get the Topic list
	 * 
	 * @param firstPage is first page
	 * @param start the start score in the Redis sorted set
	 * @return Topic list
	 */
	private List<Topic> getTopicList(boolean firstPage, int start) {
		return getPageList(TopicQuery.REDIS_TOPIC_MAP, TopicQuery.REDIS_TOPIC_SET, firstPage, start, Topic.class);
	}

	@Override
	public int insertTopic(Topic topic) {
		long newTopicId = getRedis().increase(TopicQuery.REDIS_TOPIC_ID);
		topic.setTopicId(newTopicId);
		return redisInsert(TopicQuery.REDIS_TOPIC, String.valueOf(newTopicId), topic, true);
	}

	@Override
	public int deleteTopic(int topicId) {
		return redisDelete(TopicQuery.REDIS_TOPIC, String.valueOf(topicId), true);
	}

	@Override
	public int insertComment(TopicComment comment) {
		long newComId = getRedis().increase(TopicQuery.REDIS_TOPIC_COMMENT_ID);
		comment.setId(newComId);
		return redisInsertSub(TopicQuery.REDIS_TOPIC_COMMENT, String.valueOf(newComId), comment, "tc:" + comment.getTopicId());
	}

	@Override
	public int deleteComment(int topicId, int commentId) {
		return redisDeleteSub(TopicQuery.REDIS_TOPIC_COMMENT, String.valueOf(commentId), "tc:" + topicId);
	}

	@Override
	public int insertPraise(TopicPraise praise) {
		long newPrsId = getRedis().increase(TopicQuery.REDIS_TOPIC_PRAISE_ID);
		praise.setId(newPrsId);
		return redisInsertSub(TopicQuery.REDIS_TOPIC_PRAISE, String.valueOf(newPrsId), praise, "tp:" + praise.getTopicId());
	}

	@Override
	public int deletePraise(int topicId, int praiseId) {
		return redisDeleteSub(TopicQuery.REDIS_TOPIC_PRAISE, String.valueOf(praiseId), "tp:" + topicId);
	}
}