package com.albanote.memberservice.service

import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.cloudfront.AmazonCloudFront
import com.amazonaws.services.cloudfront.AmazonCloudFrontClientBuilder
import com.amazonaws.services.cloudfront.model.CreateInvalidationRequest
import com.amazonaws.services.cloudfront.model.InvalidationBatch
import com.amazonaws.services.cloudfront.model.Paths
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.s3.internal.Mimetypes
import com.amazonaws.services.s3.model.CannedAccessControlList
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.model.PutObjectRequest
import com.amazonaws.util.IOUtils.toByteArray
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.ByteArrayInputStream
import java.time.LocalDateTime
import javax.annotation.PostConstruct

@Service
class S3Service {
    @PostConstruct
    fun init() {
        val credentials: AWSCredentials = BasicAWSCredentials(accessKey, secretKey)
        s3Client = AmazonS3ClientBuilder.standard()
            .withCredentials(AWSStaticCredentialsProvider(credentials))
            .withRegion(region)
            .build()
        cloudFrontClient = AmazonCloudFrontClientBuilder.standard()
            .withCredentials(AWSStaticCredentialsProvider(credentials))
            .withRegion(region)
            .build()
    }

    lateinit var s3Client: AmazonS3

    lateinit var cloudFrontClient: AmazonCloudFront

    @Value("\${cloud.aws.credentials.accessKey}")
    lateinit var accessKey: String

    @Value("\${cloud.aws.credentials.secretKey}")
    lateinit var secretKey: String

    @Value("\${cloud.aws.s3.bucket}")
    lateinit var bucket: String

    @Value("\${cloud.aws.region.static}")
    lateinit var region: String

    @Value("\${cloud.aws.cloud-front.domain-name}")
    lateinit var cloudFrontDomainName: String

    @Value("\${cloud.aws.cloud-front.distributionId}")
    lateinit var distributionId: String

    private val jpegFileType = "jpeg"
    private val zipFileType = "zip"
    private val pdfFileType = "pdf"

    val memberProfileImage = "member_profile_image"
    val workplaceImage = "workplace_image"
    val workplaceTodoImage = "workplace_todo_image"

    fun imageUpload(
        file: MultipartFile,
        id: Long,
        folderName: String,
        numbering: Boolean = false,
        prefix: String? = null
    ): String {
        return imageUpload(file, id.toString(), folderName, numbering, prefix)
    }

    fun imageUpload(
        file: MultipartFile,
        id: String,
        folderName: String,
        numbering: Boolean = false,
        prefix: String? = null
    ): String {
        return imageUploadReturnSize(file, id, folderName, numbering, prefix).first
    }

    fun imageUploadReturnSize(
        file: MultipartFile,
        id: String,
        folderName: String,
        numbering: Boolean = false,
        prefix: String? = null
    ): Pair<String, Long> {
        var fileName = "$folderName/${if (prefix != null) prefix + id else id}"

        val objMeta = ObjectMetadata()

        val bytes: ByteArray = toByteArray(file.inputStream)
        val byteArrayIs = ByteArrayInputStream(bytes)
        objMeta.contentLength = bytes.size.toLong()
        objMeta.contentType = Mimetypes.getInstance().getMimetype(file.originalFilename)

        val fileType = when {
            objMeta.contentType.contains("image") -> jpegFileType
            objMeta.contentType.contains("zip") -> zipFileType
            objMeta.contentType.contains("pdf") -> pdfFileType
            else -> ""
        }

        if (numbering) {
            var isExist = true
            var imageCount = 0

            while (isExist) {
                imageCount++
                isExist = s3Client.doesObjectExist(bucket, "${fileName}_${imageCount}.$fileType")
            }

            fileName += "_$imageCount"

            s3Client.putObject(
                PutObjectRequest(bucket, "$fileName.$fileType", byteArrayIs, objMeta)
                    .withCannedAcl(CannedAccessControlList.PublicRead)
            )
        } else {
            val isExist = s3Client.doesObjectExist(bucket, "$fileName.$fileType")

            s3Client.putObject(
                PutObjectRequest(bucket, "$fileName.$fileType", byteArrayIs, objMeta)
                    .withCannedAcl(CannedAccessControlList.PublicRead)
            )

            if (isExist) unCacheImage("$fileName.$fileType")
        }

        return Pair("$fileName.$fileType", objMeta.contentLength)
    }

    fun convertCloudFrontUrl(path: String?): String? {
        return if (path == null) null else "https://${cloudFrontDomainName}/$path"
    }

//    fun deleteImage(id: Long?, folderName: String, numbering: Boolean = false, prefix: String? = null): Boolean {
//        val fileName = "$folderName/${if (prefix != null) prefix + id else id}"
//
//        return if (numbering) {
//            var isExist = true
//            var imageCount = 0
//
//            while (isExist) {
//                imageCount++
//                isExist = s3Client.doesObjectExist(bucket, "${fileName}_${imageCount}.$fileType")
//                s3Client.deleteObject(bucket, "${fileName}_${imageCount}.$fileType")
//            }
//            true
//        } else {
//            s3Client.deleteObject(bucket, "${fileName}.$fileType")
//            true
//        }
//    }

    fun deleteImage(url: String) {
        s3Client.deleteObject(bucket, url)
    }

    /**
     * CloudFront 이미지 캐시 무효화
     */
    fun unCacheImage(path: String) {
        val invalidationPaths = Paths().withItems("/$path").withQuantity(1)
        val invalidationBatch = InvalidationBatch(invalidationPaths, LocalDateTime.now().toString())
        val invalidationRequest = CreateInvalidationRequest(distributionId, invalidationBatch)

        cloudFrontClient.createInvalidation(invalidationRequest)
    }
}