<template>
  <div class="abandoned-pet">
    <el-card class="search-card">
      <el-form :model="searchForm" inline>
        <el-form-item label="宠物名称">
          <el-input v-model="searchForm.name" placeholder="请输入宠物名称" clearable />
        </el-form-item>
        <el-form-item label="领养状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="待领养" value="待领养" />
            <el-option label="已领养" value="已领养" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 添加/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="100px">
        <el-form-item label="宠物名称" prop="petName">
          <el-input v-model="form.petName" placeholder="请输入宠物名称" />
        </el-form-item>
        <el-form-item label="品种ID" prop="breedId">
          <el-input-number v-model="form.breedId" :min="1" placeholder="请选择品种" />
        </el-form-item>
        <el-form-item label="年龄" prop="age">
          <el-input-number v-model="form.age" :min="0" placeholder="请输入年龄" />
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-select v-model="form.gender" placeholder="请选择性别">
            <el-option label="公" value="公" />
            <el-option label="母" value="母" />
          </el-select>
        </el-form-item>
        <el-form-item label="健康状况" prop="healthStatus">
          <el-input v-model="form.healthStatus" placeholder="请输入健康状况" />
        </el-form-item>
        <el-form-item label="弃养原因" prop="abandonReason">
          <el-input v-model="form.abandonReason" type="textarea" :rows="3" placeholder="请输入弃养原因" />
        </el-form-item>
        <el-form-item label="救助地址" prop="rescueAddress">
          <el-input v-model="form.rescueAddress" placeholder="请输入救助地址" />
        </el-form-item>
        <el-form-item label="宠物照片" prop="petPhoto">
          <el-upload
            class="photo-uploader"
            :action="uploadUrl"
            :show-file-list="false"
            :on-success="handleUploadSuccess"
            :before-upload="beforeUpload"
          >
            <img v-if="form.petPhoto" :src="form.petPhoto" class="photo-preview" />
            <el-icon v-else class="photo-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item label="领养状态" prop="adoptStatus">
          <el-select v-model="form.adoptStatus" placeholder="请选择领养状态">
            <el-option label="待领养" value="待领养" />
            <el-option label="已领养" value="已领养" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <el-card class="list-card">
      <template #header>
        <div class="card-header">
          <span>宠物列表</span>
          <el-button type="primary" @click="handleAdd">添加宠物</el-button>
        </div>
      </template>
      
      <div class="pet-grid">
        <div v-for="pet in tableData" :key="pet.petId" class="pet-card">
          <div class="pet-image">
            <el-image
              v-if="pet.petPhoto"
              :src="pet.petPhoto"
              :preview-src-list="[pet.petPhoto]"
              fit="cover"
            />
            <div v-else class="no-image">无照片</div>
          </div>
          
          <div class="pet-info">
            <h3 class="pet-name">{{ pet.petName }}</h3>
            <div class="pet-details">
              <div class="detail-item">
                <span class="label">品种ID:</span>
                <span>{{ pet.breedId }}</span>
              </div>
              <div class="detail-item">
                <span class="label">年龄:</span>
                <span>{{ pet.age }}岁</span>
              </div>
              <div class="detail-item">
                <span class="label">性别:</span>
                <span>{{ pet.gender }}</span>
              </div>
              <div class="detail-item">
                <span class="label">健康状况:</span>
                <span>{{ pet.healthStatus }}</span>
              </div>
              <div class="detail-item">
                <span class="label">弃养原因:</span>
                <span>{{ pet.abandonReason }}</span>
              </div>
              <div class="detail-item">
                <span class="label">救助地址:</span>
                <span>{{ pet.rescueAddress }}</span>
              </div>
              <div class="detail-item">
                <span class="label">领养状态:</span>
                <el-tag :type="pet.adoptStatus === '已领养' ? 'success' : 'warning'" size="small">
                  {{ pet.adoptStatus }}
                </el-tag>
              </div>
            </div>
            
            <div class="pet-actions">
              <el-button type="primary" size="small" @click="handleEdit(pet)">编辑</el-button>
              <el-button type="danger" size="small" @click="handleDelete(pet)">删除</el-button>
            </div>
          </div>
        </div>
      </div>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.currentPage"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getPetPage, deletePet, addPet, updatePet } from '@/api/abandonedPet'

const uploadUrl = ref('/api/upload')

const searchForm = reactive({
  name: '',
  status: ''
})

const tableData = ref([])
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)
const form = reactive({
  petId: null,
  breedId: null,
  petName: '',
  age: null,
  gender: '',
  healthStatus: '',
  abandonReason: '',
  rescueAddress: '',
  petPhoto: '',
  adoptStatus: '待领养',
  remark: ''
})

const formRules = {
  petName: [
    { required: true, message: '请输入宠物名称', trigger: 'blur' }
  ],
  breedId: [
    { required: true, message: '请选择品种', trigger: 'change' }
  ],
  age: [
    { required: true, message: '请输入年龄', trigger: 'blur' }
  ],
  gender: [
    { required: true, message: '请选择性别', trigger: 'change' }
  ],
  healthStatus: [
    { required: true, message: '请输入健康状况', trigger: 'blur' }
  ],
  adoptStatus: [
    { required: true, message: '请选择领养状态', trigger: 'change' }
  ]
}

const handleUploadSuccess = (response) => {
  if (response.code === 200) {
    form.petPhoto = response.data
    ElMessage.success('照片上传成功')
  } else {
    ElMessage.error(response.msg || '照片上传失败')
  }
}

const beforeUpload = (file) => {
  const isJPG = file.type === 'image/jpeg' || file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isJPG) {
    ElMessage.error('上传图片只能是 JPG/PNG 格式!')
  }
  if (!isLt2M) {
    ElMessage.error('上传图片大小不能超过 2MB!')
  }
  return isJPG && isLt2M
}



const loadData = async () => {
  try {
    const res = await getPetPage({
      current: pagination.currentPage,
      size: pagination.pageSize,
      petName: searchForm.name,
      adoptStatus: searchForm.status
    })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    // 错误信息已在 request.js 的响应拦截器中处理
  }
}

const handleSearch = () => {
  pagination.currentPage = 1
  loadData()
}

const handleReset = () => {
  searchForm.name = ''
  searchForm.status = ''
  handleSearch()
}

const handleAdd = () => {
  dialogTitle.value = '添加宠物'
  Object.assign(form, {
    petId: null,
    breedId: null,
    petName: '',
    age: null,
    gender: '',
    healthStatus: '',
    abandonReason: '',
    rescueAddress: '',
    petPhoto: '',
    adoptStatus: '待领养',
    remark: ''
  })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑宠物'
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleDialogClose = () => {
  formRef.value?.resetFields()
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (form.petId) {
          await updatePet(form)
          ElMessage.success('更新成功')
        } else {
          await addPet(form)
          ElMessage.success('添加成功')
        }
        dialogVisible.value = false
        loadData()
      } catch (error) {
        // 错误信息已在 request.js 的响应拦截器中处理
      }
    }
  })
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除宠物"${row.name}"吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deletePet(row.petId)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    // 用户取消操作
  }
}

const handleSizeChange = (val) => {
  pagination.pageSize = val
  loadData()
}

const handleCurrentChange = (val) => {
  pagination.currentPage = val
  loadData()
}

onMounted(() => {
  loadData()
})
</script>

<style scoped lang="scss">
.abandoned-pet {
  .search-card {
    margin-bottom: 20px;
  }

  .list-card {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }

    .pet-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
      gap: 20px;
      margin-bottom: 20px;
    }

    .pet-card {
      border: 1px solid #ebeef5;
      border-radius: 4px;
      overflow: hidden;
      transition: all 0.3s;
      background: #fff;
      box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);

      &:hover {
        box-shadow: 0 4px 16px 0 rgba(0, 0, 0, 0.15);
        transform: translateY(-2px);
      }
    }

    .pet-image {
      width: 100%;
      height: 200px;
      background-color: #f5f7fa;
      display: flex;
      align-items: center;
      justify-content: center;
      overflow: hidden;

      .el-image {
        width: 100%;
        height: 100%;
      }

      .no-image {
        color: #909399;
        font-size: 14px;
      }
    }

    .pet-info {
      padding: 16px;
    }

    .pet-name {
      margin: 0 0 12px 0;
      font-size: 18px;
      color: #303133;
      font-weight: 600;
    }

    .pet-details {
      .detail-item {
        display: flex;
        margin-bottom: 8px;
        font-size: 14px;
        color: #606266;

        .label {
          color: #909399;
          margin-right: 8px;
          min-width: 70px;
        }
      }
    }

    .pet-actions {
      margin-top: 16px;
      display: flex;
      gap: 8px;
      justify-content: flex-end;
    }

    .pagination-container {
      margin-top: 20px;
      display: flex;
      justify-content: flex-end;
    }
  }

  .photo-uploader {
    :deep(.el-upload) {
      border: 1px dashed #d9d9d9;
      border-radius: 6px;
      cursor: pointer;
      position: relative;
      overflow: hidden;
      transition: all 0.3s;

      &:hover {
        border-color: #409eff;
      }
    }

    :deep(.el-upload) {
      width: 178px;
      height: 178px;
      display: flex;
      align-items: center;
      justify-content: center;
    }
  }

  .photo-uploader-icon {
    font-size: 28px;
    color: #8c939d;
  }

  .photo-preview {
    width: 178px;
    height: 178px;
    display: block;
  }
}
</style>
