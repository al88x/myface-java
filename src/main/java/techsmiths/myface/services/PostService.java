package techsmiths.myface.services;

import org.jdbi.v3.core.mapper.reflect.BeanMapper;
import org.springframework.stereotype.Service;
import techsmiths.myface.models.apimodels.CreatePostModel;
import techsmiths.myface.models.dbmodels.Post;
import techsmiths.myface.models.dbmodels.Receiver;
import techsmiths.myface.models.dbmodels.Sender;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService extends DatabaseService {

    public List<Post> getAllPosts(int limit, int offset) {
        return jdbi.withHandle(handle ->
                new ArrayList<>(handle.createQuery("" +
                            "SELECT " +
                                "post.id as post_id, " +
                                "post.message as post_message, " +
                                "post.image as post_image, " +
                                "sender.id as sender_id, " +
                                "sender.username as sender_username, " +
                                "sender.email as sender_email, " +
                                "sender.first_name as sender_first_name, " +
                                "sender.last_name as sender_last_name, " +
                                "sender.profile_image as sender_profile_image, " +
                                "sender.banner_image as sender_banner_image, " +
                                "receiver.id as receiver_id, " +
                                "receiver.username as receiver_username, " +
                                "receiver.email as receiver_email, " +
                                "receiver.first_name as receiver_first_name, " +
                                "receiver.last_name as receiver_last_name, " +
                                "receiver.profile_image as receiver_profile_image, " +
                                "receiver.banner_image as receiver_banner_image " +
                            "FROM posts as post " +
                            "JOIN users as sender on post.sender_user_id = sender.id " +
                            "JOIN users as receiver on post.receiver_user_id = receiver.id " +
                            "LIMIT :limit OFFSET :offset")
                        .bind("limit", limit)
                        .bind("offset", offset)
                        .registerRowMapper(BeanMapper.factory(Post.class, "post"))
                        .registerRowMapper(BeanMapper.factory(Sender.class, "sender"))
                        .registerRowMapper(BeanMapper.factory(Receiver.class, "receiver"))
                        .reduceRows(new LinkedHashMap<Integer, Post>(), (map, row) -> {
                            Post post = map.computeIfAbsent(
                                    row.getColumn("post_id", Integer.class), id -> row.getRow(Post.class)
                            );

                            post.setSender(row.getRow(Sender.class));
                            post.setReceiver(row.getRow(Receiver.class));

                            return map;
                        })
                        .values())
        );
    }

    public void createPost(CreatePostModel post) {
        jdbi.withHandle(handle ->
                handle.createUpdate(
                        "INSERT INTO posts " +
                                "(sender_user_id, receiver_user_id, message, image) " +
                                "VALUES " +
                                "(:senderUserId, :receiverUserId, :message, :image)")
                        .bind("senderUserId", post.getSenderId())
                        .bind("receiverUserId", post.getReceiverId())
                        .bind("message", post.getMessage())
                        .bind("image", post.getImage())
                        .execute()
        );
    }
}
