package com.ski.resort.mapper;

import com.ski.resort.entity.FAQ;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * FAQ常见问题Mapper
 *
 * @author XXX
 * @date 2025-10-24
 */
@Mapper
public interface FAQMapper {

    /**
     * 查询所有启用的FAQ
     */
    @Select("SELECT * FROM t_faq WHERE status = 1 AND is_deleted = 0 ORDER BY is_hot DESC, view_count DESC")
    List<FAQ> findAllEnabled();

    /**
     * 根据分类查询FAQ
     */
    @Select("SELECT * FROM t_faq WHERE category = #{category} AND status = 1 AND is_deleted = 0 ORDER BY view_count DESC")
    List<FAQ> findByCategory(@Param("category") String category);

    /**
     * 根据关键词搜索FAQ（模糊匹配）
     */
    @Select("SELECT * FROM t_faq WHERE (question LIKE CONCAT('%', #{keyword}, '%') OR keywords LIKE CONCAT('%', #{keyword}, '%')) " +
            "AND status = 1 AND is_deleted = 0 ORDER BY view_count DESC LIMIT #{limit}")
    List<FAQ> searchByKeyword(@Param("keyword") String keyword, @Param("limit") int limit);

    /**
     * 获取热门FAQ
     */
    @Select("SELECT * FROM t_faq WHERE is_hot = 1 AND status = 1 AND is_deleted = 0 ORDER BY view_count DESC LIMIT #{limit}")
    List<FAQ> findHotFAQ(@Param("limit") int limit);

    /**
     * 增加浏览次数
     */
    @Update("UPDATE t_faq SET view_count = view_count + 1 WHERE id = #{id}")
    int incrementViewCount(@Param("id") Long id);

    /**
     * 增加点赞次数
     */
    @Update("UPDATE t_faq SET like_count = like_count + 1 WHERE id = #{id}")
    int incrementLikeCount(@Param("id") Long id);

    /**
     * 根据ID查询FAQ
     */
    @Select("SELECT * FROM t_faq WHERE id = #{id} AND is_deleted = 0")
    FAQ findById(@Param("id") Long id);

    /**
     * 插入FAQ（管理员功能）
     */
    @Insert("INSERT INTO t_faq (category, question, answer, keywords, view_count, like_count, is_hot, status, " +
            "create_time, update_time, is_deleted) VALUES (#{category}, #{question}, #{answer}, #{keywords}, 0, 0, " +
            "#{isHot}, #{status}, NOW(), NOW(), 0)")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(FAQ faq);

    /**
     * 更新FAQ（管理员功能）
     */
    @Update("UPDATE t_faq SET category = #{category}, question = #{question}, answer = #{answer}, " +
            "keywords = #{keywords}, is_hot = #{isHot}, status = #{status}, update_time = NOW() WHERE id = #{id}")
    int update(FAQ faq);

    /**
     * 删除FAQ（逻辑删除，管理员功能）
     */
    @Update("UPDATE t_faq SET is_deleted = 1, update_time = NOW() WHERE id = #{id}")
    int deleteById(@Param("id") Long id);
}







