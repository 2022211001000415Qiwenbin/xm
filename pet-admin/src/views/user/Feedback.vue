
<template>
  <div class="feedback-container">
    <el-card class="submit-card">
      <template #header>
        <div class="card-header">
          <span>意见反馈</span>
        </div>
      </template>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入反馈标题" maxlength="50" show-word-limit />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="5" placeholder="请详细描述您的意见或建议" maxlength="500" show-word-limit />
        </el-form-item>
        <el-form-item label="联系方式" prop="contact">
          <el-input v-model="form.contact" placeholder="请输入联系方式（选填）" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSubmit">提交反馈</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="list-card">
      <template #header>
        <div class="card-header">
          <span>我的反馈记录</span>
        </div>
      </template>
      <el-table :data="feedbackList" stripe style="width: 100%" v-loading="loading">
        <el-table-column prop="title" label="标题" min-width="120" />
        <el-table-column prop="content" label="内容" min-width="200" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)" size="small">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="提交时间" width="170" />
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleDetail(row)">查看</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :total="pagination.total"
          :page-sizes="[5, 10, 20]"
          layout="total, sizes, prev, pager, next"
          @size-change="loadFeedbackList"
          @current-change="loadFeedbackList"
        />
      </div>
    </el-card>

    <!-- 详情对话框 -->
    <el-dialog v-model="detailVisible" title="反馈详情" width="550px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="标题">{{ currentFeedback.title }}</el-descriptions-item>
        <el-descriptions-item label="内容">{{ currentFeedback.content }}</el-descriptions-item>
        <el-descriptions-item label="联系方式">{{ currentFeedback.contact || '未填写' }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="statusTagType(currentFeedback.status)">{{ currentFeedback.status }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="提交时间">{{ currentFeedback.createTime }}</el-descriptions-item>
        <el-descriptions-item label="管理员回复" v-if="currentFeedback.reply">
          {{ currentFeedback.reply }}
        </el-descriptions-item>
        <el-descriptions-item label="回复时间" v-if="currentFeedback.replyTime">
          {{ currentFeedback.replyTime }}
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useStore } from 'vuex'
import { ElMessage } from 'element-plus'
import { submitFeedback, getFeedbackByUser } from '@/api/feedback'

const store = useStore()
const formRef = ref(null)
const loading = ref(false)
const feedbackList = ref([])
const detailVisible = ref(false)
const currentFeedback = ref({})

const form = reactive({
  title: '',
  content: '',
  contact: '',
  userId: store.state.user.userId
})

const rules = {
  title: [
    { required: true, message: '请输入反馈标题', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入反馈内容', trigger: 'blur' }
  ]
}

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const statusTagType = (status) => {
  switch (status) {
    case '待处理': return 'warning'
    case '处理中': return 'primary'
    case '已处理': return 'success'
    default: return 'info'
  }
}

const loadFeedbackList = async () => {
  loading.value = true
  try {
    const res = await getFeedbackByUser(store.state.user.userId, {
      current: pagination.current,
      size: pagination.size
    })
    feedbackList.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    ElMessage.error('获取反馈列表失败')
  } finally {
    loading.value = false
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        await submitFeedback(form)
        ElMessage.success('反馈提交成功')
        resetForm()
        loadFeedbackList()
      } catch (error) {
        ElMessage.error('提交失败')
      }
    }
  })
}

const resetForm = () => {
  form.title = ''
  form.content = ''
  form.contact = ''
}

const handleDetail = (row) => {
  currentFeedback.value = row
  detailVisible.value = true
}

onMounted(() => {
  loadFeedbackList()
})
</script>

<style scoped lang="scss">
.feedback-container {

  .page-title {
    font-size: 24px;
    font-weight: 800;
    color: #1F2937;
    margin: 0 0 24px;
  }

  .submit-card {
    margin-bottom: 24px;
    border-radius: 16px;
    border: 1px solid #F3F4F6;

    .card-header {
      font-size: 18px;
      font-weight: 700;
      color: #1F2937;
    }

    :deep(.el-button--primary) {
      background: linear-gradient(135deg, #FF8A65, #FF6B35);
      border: none;
      border-radius: 10px;
      font-weight: 600;
    }

    :deep(.el-button--default) {
      border-radius: 10px;
    }

    :deep(.el-input__wrapper),
    :deep(.el-textarea__inner) {
      border-radius: 10px;
    }
  }

  .list-card {
    border-radius: 16px;
    border: 1px solid #F3F4F6;

    .card-header {
      font-size: 18px;
      font-weight: 700;
      color: #1F2937;
    }

    :deep(.el-table) {
      border-radius: 12px;
      overflow: hidden;

      th.el-table__cell {
        background: #FAFAF8;
        color: #6B7280;
        font-weight: 600;
      }
    }
  }

  .pagination-wrapper {
    display: flex;
    justify-content: center;
    margin-top: 20px;

    :deep(.el-pager li.is-active) {
      background: linear-gradient(135deg, #FF8A65, #FF6B35);
      border-radius: 8px;
    }
  }
}
</style>
