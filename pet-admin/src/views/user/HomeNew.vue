
<template>
  <div class="home-container">
    <el-card class="search-card">
      <el-form :model="searchForm" inline>
        <el-form-item label="宠物名称">
          <el-input v-model="searchForm.petName" placeholder="请输入宠物名称" clearable />
        </el-form-item>
        <el-form-item label="领养状态">
          <el-select v-model="searchForm.adoptStatus" placeholder="请选择状态" clearable>
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

    <el-card class="pet-list-card">
      <template #header>
        <div class="card-header">
          <span>待领养宠物</span>
        </div>
      </template>
      <el-row :gutter="20">
        <el-col v-for="pet in tableData" :key="pet.petId" :xs="24" :sm="12" :md="8" :lg="6">
          <el-card class="pet-card" shadow="hover">
            <div class="pet-image">
              <el-image
                :src="pet.petPhoto || 'https://via.placeholder.com/300x200?text=' + pet.petName"
                fit="cover"
              />
            </div>
            <div class="pet-info">
              <h3>{{ pet.petName }}</h3>
              <div class="pet-details">
                <p><span class="label">品种ID：</span>{{ pet.breedId }}</p>
                <p><span class="label">年龄：</span>{{ pet.age }}岁</p>
                <p><span class="label">性别：</span>{{ pet.gender }}</p>
                <p><span class="label">健康状况：</span>{{ pet.healthStatus }}</p>
                <p><span class="label">救助地址：</span>{{ pet.rescueAddress }}</p>
                <p><span class="label">弃养原因：</span>{{ pet.abandonReason }}</p>
              </div>
              <div class="pet-status">
                <el-tag :type="pet.adoptStatus === '已领养' ? 'success' : 'warning'">
                  {{ pet.adoptStatus }}
                </el-tag>
              </div>
              <div class="pet-actions">
                <el-button type="primary" size="small" @click="handleViewDetail(pet)">查看详情</el-button>
                <el-button
                  v-if="pet.adoptStatus === '待领养'"
                  type="success"
                  size="small"
                  @click="handleAdopt(pet)"
                >
                  申请领养
                </el-button>
                <el-button
                  v-if="pet.adoptStatus === '待领养'"
                  type="info"
                  size="small"
                  @click="handleReserve(pet)"
                >
                  预约看宠
                </el-button>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.currentPage"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[12, 24, 36, 48]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 宠物详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      :title="currentPet.petName"
      width="600px"
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item label="宠物名称">{{ currentPet.petName }}</el-descriptions-item>
        <el-descriptions-item label="品种ID">{{ currentPet.breedId }}</el-descriptions-item>
        <el-descriptions-item label="年龄">{{ currentPet.age }}岁</el-descriptions-item>
        <el-descriptions-item label="性别">{{ currentPet.gender }}</el-descriptions-item>
        <el-descriptions-item label="健康状况">{{ currentPet.healthStatus }}</el-descriptions-item>
        <el-descriptions-item label="弃养原因">{{ currentPet.abandonReason }}</el-descriptions-item>
        <el-descriptions-item label="救助地址" :span="2">{{ currentPet.rescueAddress }}</el-descriptions-item>
        <el-descriptions-item label="领养状态" :span="2">
          <el-tag :type="currentPet.adoptStatus === '已领养' ? 'success' : 'warning'">
            {{ currentPet.adoptStatus }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ currentPet.remark || '无' }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
        <el-button
          v-if="currentPet.adoptStatus === '待领养'"
          type="primary"
          @click="handleAdopt(currentPet)"
        >
          申请领养
        </el-button>
        <el-button
          v-if="currentPet.adoptStatus === '待领养'"
          type="info"
          @click="handleReserve(currentPet)"
        >
          预约看宠
        </el-button>
      </template>
    </el-dialog>

    <!-- 领养申请对话框 -->
    <el-dialog
      v-model="adoptDialogVisible"
      title="申请领养"
      width="500px"
    >
      <el-form ref="adoptFormRef" :model="adoptForm" :rules="adoptRules" label-width="100px">
        <el-form-item label="宠物名称">
          <el-input v-model="currentPet.petName" disabled />
        </el-form-item>
        <el-form-item label="申请信息" prop="applyInfo">
          <el-input
            v-model="adoptForm.applyInfo"
            type="textarea"
            :rows="4"
            placeholder="请输入申请信息，说明您的养宠经验和领养意愿"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="adoptDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitAdopt">提交申请</el-button>
      </template>
    </el-dialog>

    <!-- 预约对话框 -->
    <el-dialog
      v-model="reserveDialogVisible"
      title="预约看宠"
      width="500px"
    >
      <el-form ref="reserveFormRef" :model="reserveForm" :rules="reserveRules" label-width="100px">
        <el-form-item label="宠物名称">
          <el-input v-model="currentPet.petName" disabled />
        </el-form-item>
        <el-form-item label="预约时间" prop="reserveTime">
          <el-date-picker
            v-model="reserveForm.reserveTime"
            type="datetime"
            placeholder="选择预约时间"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DDTHH:mm:ss"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="预约地址" prop="reserveAddress">
          <el-input v-model="reserveForm.reserveAddress" placeholder="请输入预约地址" />
        </el-form-item>
        <el-form-item label="联系人" prop="contactPerson">
          <el-input v-model="reserveForm.contactPerson" placeholder="请输入联系人姓名" />
        </el-form-item>
        <el-form-item label="联系电话" prop="contactPhone">
          <el-input v-model="reserveForm.contactPhone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="备注" prop="reserveRemark">
          <el-input
            v-model="reserveForm.reserveRemark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注信息"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="reserveDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitReserve">提交预约</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getPetPage, submitAdoption, submitReservation } from '@/api/pet'
import { useStore } from 'vuex'

const store = useStore()

const searchForm = reactive({
  petName: '',
  adoptStatus: '待领养'
})

const tableData = ref([])
const pagination = reactive({
  currentPage: 1,
  pageSize: 12,
  total: 0
})

const detailDialogVisible = ref(false)
const adoptDialogVisible = ref(false)
const reserveDialogVisible = ref(false)
const currentPet = ref({})

const adoptFormRef = ref(null)
const adoptForm = reactive({
  applyInfo: ''
})

const adoptRules = {
  applyInfo: [
    { required: true, message: '请输入申请信息', trigger: 'blur' },
    { min: 10, message: '申请信息不能少于10个字符', trigger: 'blur' }
  ]
}

const reserveFormRef = ref(null)
const reserveForm = reactive({
  reserveTime: '',
  reserveAddress: '',
  contactPerson: '',
  contactPhone: '',
  reserveRemark: ''
})

const reserveRules = {
  reserveTime: [
    { required: true, message: '请选择预约时间', trigger: 'change' }
  ],
  reserveAddress: [
    { required: true, message: '请输入预约地址', trigger: 'blur' }
  ],
  contactPerson: [
    { required: true, message: '请输入联系人姓名', trigger: 'blur' }
  ],
  contactPhone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ]
}

const loadData = async () => {
  try {
    const res = await getPetPage({
      current: pagination.currentPage,
      size: pagination.pageSize,
      petName: searchForm.petName,
      adoptStatus: searchForm.adoptStatus
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
  searchForm.petName = ''
  searchForm.adoptStatus = '待领养'
  handleSearch()
}

const handleViewDetail = (pet) => {
  currentPet.value = { ...pet }
  detailDialogVisible.value = true
}

const handleAdopt = (pet) => {
  currentPet.value = { ...pet }
  adoptForm.applyInfo = ''
  adoptDialogVisible.value = true
}

const handleReserve = (pet) => {
  currentPet.value = { ...pet }
  reserveForm.reserveTime = ''
  reserveForm.reserveAddress = pet.rescueAddress
  reserveForm.contactPerson = ''
  reserveForm.contactPhone = ''
  reserveForm.reserveRemark = ''
  reserveDialogVisible.value = true
}

const handleSubmitAdopt = async () => {
  if (!adoptFormRef.value) return
  await adoptFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        await submitAdoption({
          userId: store.state.user.userId,
          petId: currentPet.value.petId,
          applyInfo: adoptForm.applyInfo
        })
        ElMessage.success('领养申请提交成功，请等待审核')
        adoptDialogVisible.value = false
        loadData()
      } catch (error) {
        // 错误信息已在 request.js 的响应拦截器中处理
      }
    }
  })
}

const handleSubmitReserve = async () => {
  if (!reserveFormRef.value) return
  await reserveFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        await submitReservation({
          userId: store.state.user.userId,
          petId: currentPet.value.petId,
          reserveTime: reserveForm.reserveTime,
          reserveAddress: reserveForm.reserveAddress,
          contactPerson: reserveForm.contactPerson,
          contactPhone: reserveForm.contactPhone,
          reserveRemark: reserveForm.reserveRemark
        })
        ElMessage.success('预约提交成功，请等待确认')
        reserveDialogVisible.value = false
        loadData()
      } catch (error) {
        // 错误信息已在 request.js 的响应拦截器中处理
      }
    }
  })
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
.home-container {
  padding: 20px;

  .search-card {
    margin-bottom: 20px;
  }

  .pet-list-card {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }

    .pet-card {
      margin-bottom: 20px;

      .pet-image {
        height: 200px;
        overflow: hidden;

        .el-image {
          width: 100%;
          height: 100%;
        }
      }

      .pet-info {
        padding: 15px 0;

        h3 {
          font-size: 18px;
          color: #333;
          margin-bottom: 10px;
        }

        .pet-details {
          p {
            font-size: 14px;
            color: #666;
            margin: 5px 0;

            .label {
              color: #999;
            }
          }
        }

        .pet-status {
          margin: 10px 0;
        }

        .pet-actions {
          display: flex;
          gap: 10px;
          margin-top: 10px;

          .el-button {
            flex: 1;
          }
        }
      }
    }
  }

  .pagination-container {
    margin-top: 20px;
    display: flex;
    justify-content: center;
  }
}
</style>
