package org.example.dataprotal.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.example.dataprotal.dto.request.CardRequest;
import org.example.dataprotal.dto.request.SubTopicRequest;
import org.example.dataprotal.dto.request.TopicRequest;
import org.example.dataprotal.model.report.Card;
import org.example.dataprotal.model.report.SubTopic;
import org.example.dataprotal.model.report.Topic;
import org.example.dataprotal.repository.report.CardRepository;
import org.example.dataprotal.repository.report.SubTopicRepository;
import org.example.dataprotal.repository.report.TopicRepository;
import org.example.dataprotal.service.CloudinaryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/report")
@RequiredArgsConstructor
public class ReportController {


    private final TopicRepository topicRepository;
    private final CardRepository cardRepository;
    private final CloudinaryService cloudinaryService;
    private final SubTopicRepository subTopicRepository;
private final Cloudinary cloudinary;


    @GetMapping("/topics")
    public ResponseEntity<?> getReportTops() {
        List<Topic> topic = topicRepository.findAll();
        return ResponseEntity.ok(topic);
    }

    @GetMapping("/topics/{topicSlug}")
    public ResponseEntity<?> getReportSubs(@PathVariable String topicSlug) {
        List<Topic> reportTop = topicRepository.findAllByTopicSlug(topicSlug);
        return ResponseEntity.ok(reportTop);
    }

    @GetMapping("{topicSlug}/{subTopicSlug}")
    public ResponseEntity<?> getReportCards(@PathVariable String topicSlug, @PathVariable String subTopicSlug) {
        List<Card> cards = cardRepository.findByTopicSlugAndSubTopicSlug(topicSlug, subTopicSlug);
        return ResponseEntity.ok(cards);
    }

        @PostMapping("/topic/add")
        public ResponseEntity<?> createReportTop(@RequestPart TopicRequest topicRequest, @RequestPart MultipartFile multipartFile) throws IOException {
            Topic topic = new Topic();
            topic.setTopic(topicRequest.getTopic());
            topic.setTopicIcon(cloudinaryService.uploadFile(multipartFile, "Topic"));
            topic.generateTopicSlug();
            return ResponseEntity.status(201).body(topicRepository.save(topic));
        }

    @PostMapping("/subTopic/add")
    public ResponseEntity<?> createReportSub(@RequestBody SubTopicRequest subTopicRequest) {
        SubTopic subTopic = new SubTopic();
        subTopic.setTopic(subTopicRequest.getSubTopic());
        subTopic.setSubTopic(subTopicRequest.getTopic());
        subTopic.generateSubTopicSlug();
        subTopic.generateTopicSlug();
        return ResponseEntity.status(201).body(subTopicRepository.save(subTopic));
    }

    @PostMapping("/card/add")
    public ResponseEntity<?> createCard(
            @RequestPart CardRequest cardRequest,
            @RequestPart List<MultipartFile> multipartFiles) throws IOException {

        Card card = new Card();
        card.setDescription(cardRequest.getDescription());
        card.setTitle(cardRequest.getTitle());
        card.setTopic(cardRequest.getTopic());
        card.setSubTopic(cardRequest.getSubTopic());
        card.generateTopicSlug();
        card.generateSubTopicSlug();

        List<String> fileUrls = new ArrayList<>();

        for (MultipartFile multipartFile : multipartFiles) {
            String fileName = multipartFile.getOriginalFilename();
            String contentType = multipartFile.getContentType();

            System.out.println("Dosya adı: " + fileName);
            System.out.println("Content type: " + contentType);

            Map uploadResult;
            if (contentType != null && contentType.startsWith("image")) {
                // Görsel dosya
                uploadResult = cloudinary.uploader().upload(
                        multipartFile.getBytes(),
                        ObjectUtils.asMap(
                                "resource_type", "image",
                                "folder", "CardFiles/Images"
                        )
                );
            } else {
                // Excel, PDF, vs.
                uploadResult = cloudinary.uploader().upload(
                        multipartFile.getBytes(),
                        ObjectUtils.asMap(
                                "resource_type", "raw",
                                "folder", "CardFiles/Docs"
                        )
                );
            }

            fileUrls.add((String) uploadResult.get("secure_url"));
        }

        card.setFile(fileUrls); // Eğer Card modelinde List<String> file varsa
        return ResponseEntity.status(201).body(cardRepository.save(card));
    }

}
