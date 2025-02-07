package com.js.project.controller;
import com.js.project.config.CosClientConfig;
import com.js.project.manager.CosManager;
import com.js.project.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(FileController.class)
class FileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private CosManager cosManager;

    @MockBean
    private CosClientConfig cosClientConfig;

    @Test
    public void testUploadFile() throws Exception {
        // 创建模拟文件
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test data".getBytes()
        );

        // 模拟请求参数
        String biz = "USER_AVATAR";
        String requestBody = "{\"biz\": \"" + biz + "\"}";

        // 执行请求
        mockMvc.perform(MockMvcRequestBuilders.multipart("/file/upload")
                        .file(file)
                        .param("biz", biz)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}