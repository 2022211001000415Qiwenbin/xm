
<template>
  <div class="feedback-manage-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>反馈管理</span>
        </div>
      </template>

      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable style="width: 120px">
            <el-option label="待处理" value="待处理" />
            <el-option label="处理中" value="处理中" />
            <el-option label="已处理" value="已处理" />
          </el-select>
        </el-form-item>
        <el-form-item label="标题">
          <el-input v-model="searchForm.title" placeholder="搜索标题" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 表格 -->
      <el-table :data="feedbackList" stripe style="width: 100%" v-loading="loading">
        <el-table-column prop="feedbackId" label="ID" width="70" />
        <el-table-column prop="title" label="标题" min-width="120" show-overflow-tooltip />
        <el-table-column prop="content" label="内容" min-width="200" show-overflow-tooltip />
        <el-table-column prop="userName" label="反馈用户" width="100" />
        <el-table-column prop="contact" label="联系方式" width="130" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)" size="small">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="提交时间" width="170" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleReply(row)" v-if="row.status !== '已处理'">回复</el-button>
            <el-button type="primary" link size="small" @click="handleDetail(row)">详情</el-button>
            <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :total="pagination.total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          @size-change="loadFeedbackList"
          @current-change="loadFeedbackList"
        />
      </div>
    </el-card>

    <!-- 回复对话框 -->
    <el-dialog v-model="replyVisible" title="回复反馈" width="550px">
      <el-descriptions :column="1" border class="reply-info">
        <el-descriptions-item label="标题">{{ currentFeedback.title }}</el-descriptions-item>
        <el-descriptions-item label="内容">{{ currentFeedback.content }}</el-descriptions-item>
        <el-descriptions-item label="联系方式">{{ currentFeedback.contact || '未填写' }}</el-descriptions-item>
      </el-descriptions>
      <el-form ref="replyFormRef" :model="replyForm" :rules="replyRules" label-width="80px" style="margin-top: 20px">
        <el-form-item label="回复内容" prop="reply">
          <el-input v-model="replyForm.reply" type="textarea" :rows="4" placeholder="请输入回复内容" maxlength="500" show-word-limit />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="replyVisible = false">取消</el-button>
        <el-button type="primary" @click="handleReplySubmit">确认回复</el-button>
      </template>
    </el-dialog>

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
import { ElMessage, ElMessageBox } from 'element-plus'
import { getFeedbackPage, replyFeedback, deleteFeedback } from '@/api/feedback'
import { getUserInfo } from '@/api/auth'

const store = useStore()
const loading = ref(false)
const feedbackList = ref([])
const replyVisible = ref(false)
const detailVisible = ref(false)
const currentFeedback = ref({})
const replyFormRef = ref(null)

const searchForm = reactive({
  status: '',
  title: ''
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const replyForm = reactive({
  feedbackId: null,
  reply: '',
  replyAdminId: null
})

const replyRules = {
  reply: [
    { required: true, message: '请输入回复内容', trigger: 'blur' }
  ]
}

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
    const res = await getFeedbackPage({
      current: pagination.current,
      size: pagination.size,
      status: searchForm.status || undefined,
      title: searchForm.title || undefined
    })
    feedbackList.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    ElMessage.error('获取反馈列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.current = 1
  loadFeedbackList()
}

const handleReset = () => {
  searchForm.status = ''
  searchForm.title = ''
  pagination.current = 1
  loadFeedbackList()
}

const handleReply = (row) => {
  currentFeedback.value = row
  replyForm.feedbackId = row.feedbackId
  replyForm.reply = ''
  replyForm.replyAdminId = store.state.user.userId
  replyVisible.value = true
}

const handleReplySubmit = async () => {
  if (!replyFormRef.value) return
  await replyFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        await replyFeedback(replyForm)
        ElMessage.success('回复成功')
        replyVisible.value = false
        loadFeedbackList()
      } catch (error) {
        ElMessage.error('回复失败')
      }
    }
  })
}

const handleDetail = (row) => {
  currentFeedback.value = row
  detailVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该反馈吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteFeedback(row.feedbackId)
      ElMessage.success('删除成功')
      loadFeedbackList()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

onMounted(() => {
  loadFeedbackList()
})
</script>

<style scoped lang="scss">
.feedback-manage-container {
  padding: 20px;

  .card-header {
    font-size: 16px;
    font-weight: 600;
  }

  .search-form {
    margin-bottom: 16px;
  }

  .pagination-wrapper {
    display: flex;
    justify-content: flex-end;
    margin-top: 16px;
  }

  .reply-info {
    margin-bottom: 16px;
  }
}
</style>
