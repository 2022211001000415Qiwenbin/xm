
<template>
  <div class="knowledge-detail-container">
    <el-card class="detail-card" v-loading="loading">
      <div class="detail-header">
        <h1 class="detail-title">{{ article.title }}</h1>
        <div class="detail-meta">
          <el-tag type="primary" size="small">{{ article.category }}</el-tag>
          <span>作者：{{ article.authorName }}</span>
          <span><el-icon><View /></el-icon> {{ article.viewCount }}</span>
          <span>{{ article.createTime }}</span>
        </div>
      </div>
      <el-divider />
      <div class="detail-content">{{ article.content }}</div>
    </el-card>

    <!-- 评论区 -->
    <el-card class="comment-card">
      <template #header>
        <div class="comment-header">
          <span>评论区（{{ comments.length }}条）</span>
        </div>
      </template>

      <!-- 发表评论 -->
      <div class="comment-form">
        <el-input
          v-model="commentContent"
          type="textarea"
          :rows="3"
          placeholder="写下你的评论..."
          maxlength="500"
          show-word-limit
        />
        <el-button type="primary" @click="handleComment" style="margin-top: 10px" :disabled="!commentContent.trim()">发表评论</el-button>
      </div>

      <!-- 评论列表 -->
      <div class="comment-list">
        <el-empty v-if="comments.length === 0" description="暂无评论，快来发表第一条吧" />
        <div v-for="comment in comments" :key="comment.commentId" class="comment-item">
          <div class="comment-avatar">
            <el-avatar :size="36" :src="comment.userAvatar">
              <el-icon><User /></el-icon>
            </el-avatar>
          </div>
          <div class="comment-body">
            <div class="comment-top">
              <span class="comment-user">{{ comment.userName }}</span>
              <span class="comment-time">{{ comment.createTime }}</span>
            </div>
            <p class="comment-text">{{ comment.content }}</p>
          </div>
        </div>
      </div>
    </el-card>

    <div style="margin-top: 20px">
      <el-button @click="$router.back()">返回列表</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useStore } from 'vuex'
import { ElMessage } from 'element-plus'
import { View, User } from '@element-plus/icons-vue'
import { getKnowledgeDetail, getCommentList, addComment } from '@/api/petKnowledge'

const route = useRoute()
const store = useStore()
const loading = ref(false)
const article = ref({})
const comments = ref([])
const commentContent = ref('')

const loadDetail = async () => {
  loading.value = true
  try {
    const res = await getKnowledgeDetail(route.params.id)
    article.value = res.data
  } catch (error) {
    ElMessage.error('获取文章详情失败')
  } finally {
    loading.value = false
  }
}

const loadComments = async () => {
  try {
    const res = await getCommentList(route.params.id)
    comments.value = res.data
  } catch (error) {
    ElMessage.error('获取评论失败')
  }
}

const handleComment = async () => {
  if (!commentContent.value.trim()) return
  try {
    await addComment({
      knowledgeId: route.params.id,
      userId: store.state.user.userId,
      content: commentContent.value
    })
    ElMessage.success('评论成功')
    commentContent.value = ''
    loadComments()
  } catch (error) {
    ElMessage.error('评论失败')
  }
}

onMounted(() => {
  loadDetail()
  loadComments()
})
</script>

<style scoped lang="scss">
.knowledge-detail-container {
  padding: 20px;

  .detail-card {
    margin-bottom: 20px;

    .detail-header {
      .detail-title {
        margin: 0 0 16px;
        font-size: 24px;
        color: #303133;
      }

      .detail-meta {
        display: flex;
        align-items: center;
        gap: 16px;
        color: #909399;
        font-size: 13px;

        span {
          display: flex;
          align-items: center;
          gap: 4px;
        }
      }
    }

    .detail-content {
      line-height: 2;
      font-size: 15px;
      color: #303133;
      white-space: pre-wrap;
    }
  }

  .comment-card {
    .comment-header {
      font-size: 16px;
      font-weight: 600;
    }

    .comment-form {
      margin-bottom: 24px;
    }

    .comment-list {
      .comment-item {
        display: flex;
        gap: 12px;
        padding: 16px 0;
        border-bottom: 1px solid #f0f0f0;

        &:last-child {
          border-bottom: none;
        }

        .comment-body {
          flex: 1;

          .comment-top {
            display: flex;
            align-items: center;
            gap: 12px;
            margin-bottom: 8px;

            .comment-user {
              font-weight: 600;
              color: #303133;
              font-size: 14px;
            }

            .comment-time {
              color: #c0c4cc;
              font-size: 12px;
            }
          }

          .comment-text {
            margin: 0;
            color: #606266;
            font-size: 14px;
            line-height: 1.6;
          }
        }
      }
    }
  }
}
</style>
