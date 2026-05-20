
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
          <div class="admin-actions">
            <el-button type="primary" size="small" @click="handleEdit">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete">删除文章</el-button>
          </div>
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

      <!-- 评论列表 -->
      <div class="comment-list">
        <el-empty v-if="comments.length === 0" description="暂无评论" />
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
          <el-button type="danger" link size="small" @click="handleDeleteComment(comment)">删除</el-button>
        </div>
      </div>
    </el-card>

    <div style="margin-top: 20px">
      <el-button @click="$router.back()">返回列表</el-button>
    </div>

    <!-- 编辑对话框 -->
    <el-dialog v-model="editVisible" title="编辑文章" width="700px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入文章标题" maxlength="100" show-word-limit />
        </el-form-item>
        <el-form-item label="分类" prop="category">
          <el-select v-model="form.category" placeholder="请选择分类" style="width: 100%">
            <el-option label="饲养指南" value="饲养指南" />
            <el-option label="健康护理" value="健康护理" />
            <el-option label="训练技巧" value="训练技巧" />
            <el-option label="品种介绍" value="品种介绍" />
          </el-select>
        </el-form-item>
        <el-form-item label="封面">
          <el-input v-model="form.cover" placeholder="封面图片URL（选填）" />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="12" placeholder="请输入文章内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" @click="handleUpdate">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { View, User } from '@element-plus/icons-vue'
import { getKnowledgeDetail, getCommentList, addComment, deleteComment, updateKnowledge, deleteKnowledge } from '@/api/petKnowledge'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const article = ref({})
const comments = ref([])
const editVisible = ref(false)
const formRef = ref(null)

const form = reactive({
  knowledgeId: null,
  title: '',
  content: '',
  cover: '',
  category: '',
  authorId: null
})

const rules = {
  title: [{ required: true, message: '请输入文章标题', trigger: 'blur' }],
  category: [{ required: true, message: '请选择分类', trigger: 'change' }],
  content: [{ required: true, message: '请输入文章内容', trigger: 'blur' }]
}

const loadDetail = async () => {
  loading.value = true
  try {
    const res = await getKnowledgeDetail(route.params.id, true)
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

const handleEdit = () => {
  form.knowledgeId = article.value.knowledgeId
  form.title = article.value.title
  form.content = article.value.content
  form.cover = article.value.cover
  form.category = article.value.category
  form.authorId = article.value.authorId
  editVisible.value = true
}

const handleUpdate = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        await updateKnowledge(form)
        ElMessage.success('更新成功')
        editVisible.value = false
        loadDetail()
      } catch (error) {
        ElMessage.error('更新失败')
      }
    }
  })
}

const handleDelete = () => {
  ElMessageBox.confirm('删除文章将同时删除所有评论，确定要删除吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteKnowledge(article.value.knowledgeId)
      ElMessage.success('删除成功')
      router.push('/knowledge-manage')
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

const handleDeleteComment = (comment) => {
  ElMessageBox.confirm('确定要删除该评论吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteComment(comment.commentId)
      ElMessage.success('评论已删除')
      loadComments()
    } catch (error) {
      ElMessage.error('删除评论失败')
    }
  }).catch(() => {})
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
        flex-wrap: wrap;

        span {
          display: flex;
          align-items: center;
          gap: 4px;
        }

        .admin-actions {
          margin-left: auto;
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

    .comment-list {
      .comment-item {
        display: flex;
        align-items: flex-start;
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
