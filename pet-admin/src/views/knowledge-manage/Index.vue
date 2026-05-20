
<template>
  <div class="knowledge-manage-container">
    <!-- 分类筛选 + 发布按钮 -->
    <el-card class="filter-card">
      <div class="filter-bar">
        <div class="filter-left">
          <span class="filter-label">分类：</span>
          <el-radio-group v-model="currentCategory" @change="handleCategoryChange">
            <el-radio-button label="">全部</el-radio-button>
            <el-radio-button label="饲养指南">饲养指南</el-radio-button>
            <el-radio-button label="健康护理">健康护理</el-radio-button>
            <el-radio-button label="训练技巧">训练技巧</el-radio-button>
            <el-radio-button label="品种介绍">品种介绍</el-radio-button>
          </el-radio-group>
        </div>
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon> 发布文章
        </el-button>
      </div>
    </el-card>

    <!-- 文章卡片列表 -->
    <div class="article-list" v-loading="loading">
      <el-empty v-if="!loading && articleList.length === 0" description="暂无文章" />
      <el-card
        v-for="article in articleList"
        :key="article.knowledgeId"
        class="article-card"
        shadow="hover"
        @click="goDetail(article.knowledgeId)"
      >
        <div class="article-inner">
          <div class="article-cover" v-if="article.cover">
            <el-image :src="article.cover" fit="cover" style="width: 160px; height: 120px; border-radius: 8px" />
          </div>
          <div class="article-info">
            <h3 class="article-title">{{ article.title }}</h3>
            <p class="article-desc">{{ article.content.substring(0, 100) }}...</p>
            <div class="article-meta">
              <el-tag size="small" type="primary">{{ article.category }}</el-tag>
              <span><el-icon><View /></el-icon> {{ article.viewCount }}</span>
              <span><el-icon><ChatDotRound /></el-icon> {{ article.commentCount }}</span>
              <span>{{ article.authorName }}</span>
              <span>{{ article.createTime }}</span>
            </div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 分页 -->
    <div class="pagination-wrapper">
      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="[6, 10, 20]"
        layout="total, sizes, prev, pager, next"
        @size-change="loadList"
        @current-change="loadList"
      />
    </div>

    <!-- 发布/编辑对话框 -->
    <el-dialog v-model="editVisible" :title="isEdit ? '编辑文章' : '发布文章'" width="700px" @close="resetForm">
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
        <el-button type="primary" @click="handleSubmit">{{ isEdit ? '保存' : '发布' }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useStore } from 'vuex'
import { ElMessage } from 'element-plus'
import { Plus, View, ChatDotRound } from '@element-plus/icons-vue'
import { getKnowledgePage, addKnowledge, updateKnowledge } from '@/api/petKnowledge'

const router = useRouter()
const store = useStore()
const loading = ref(false)
const articleList = ref([])
const editVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const currentCategory = ref('')

const pagination = reactive({
  current: 1,
  size: 6,
  total: 0
})

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

const loadList = async () => {
  loading.value = true
  try {
    const res = await getKnowledgePage({
      current: pagination.current,
      size: pagination.size,
      category: currentCategory.value || undefined
    })
    articleList.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    ElMessage.error('获取列表失败')
  } finally {
    loading.value = false
  }
}

const handleCategoryChange = () => {
  pagination.current = 1
  loadList()
}

const goDetail = (id) => {
  router.push(`/knowledge-manage/${id}`)
}

const handleAdd = () => {
  isEdit.value = false
  form.knowledgeId = null
  form.title = ''
  form.content = ''
  form.cover = ''
  form.category = ''
  form.authorId = store.state.user.userId
  editVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (isEdit.value) {
          await updateKnowledge(form)
          ElMessage.success('更新成功')
        } else {
          await addKnowledge(form)
          ElMessage.success('发布成功')
        }
        editVisible.value = false
        loadList()
      } catch (error) {
        ElMessage.error('操作失败')
      }
    }
  })
}

const resetForm = () => {
  form.title = ''
  form.content = ''
  form.cover = ''
  form.category = ''
}

onMounted(() => {
  loadList()
})
</script>

<style scoped lang="scss">
.knowledge-manage-container {
  padding: 20px;

  .filter-card {
    margin-bottom: 20px;

    .filter-bar {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .filter-left {
        display: flex;
        align-items: center;

        .filter-label {
          font-weight: 600;
          margin-right: 12px;
          white-space: nowrap;
        }
      }
    }
  }

  .article-list {
    min-height: 300px;
  }

  .article-card {
    margin-bottom: 16px;
    cursor: pointer;
    transition: transform 0.2s;

    &:hover {
      transform: translateY(-2px);
    }

    .article-inner {
      display: flex;
      gap: 20px;

      .article-info {
        flex: 1;
        display: flex;
        flex-direction: column;
        justify-content: space-between;

        .article-title {
          margin: 0 0 8px;
          font-size: 18px;
          color: #303133;
        }

        .article-desc {
          margin: 0 0 12px;
          color: #909399;
          font-size: 13px;
          line-height: 1.6;
        }

        .article-meta {
          display: flex;
          align-items: center;
          gap: 16px;
          color: #909399;
          font-size: 12px;

          span {
            display: flex;
            align-items: center;
            gap: 4px;
          }
        }
      }
    }
  }

  .pagination-wrapper {
    display: flex;
    justify-content: flex-end;
    margin-top: 20px;
  }
}
</style>

