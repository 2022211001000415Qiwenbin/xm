
<template>
  <div class="knowledge-container">
    <!-- 分类筛选 -->
    <el-card class="filter-card">
      <div class="filter-bar">
        <span class="filter-label">分类：</span>
        <el-radio-group v-model="currentCategory" @change="handleCategoryChange">
          <el-radio-button label="">全部</el-radio-button>
          <el-radio-button label="饲养指南">饲养指南</el-radio-button>
          <el-radio-button label="健康护理">健康护理</el-radio-button>
          <el-radio-button label="训练技巧">训练技巧</el-radio-button>
          <el-radio-button label="品种介绍">品种介绍</el-radio-button>
        </el-radio-group>
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { View, ChatDotRound } from '@element-plus/icons-vue'
import { getKnowledgeList } from '@/api/petKnowledge'

const router = useRouter()
const loading = ref(false)
const articleList = ref([])
const currentCategory = ref('')

const pagination = reactive({
  current: 1,
  size: 6,
  total: 0
})

const loadList = async () => {
  loading.value = true
  try {
    const res = await getKnowledgeList({
      current: pagination.current,
      size: pagination.size,
      category: currentCategory.value || undefined
    })
    articleList.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    ElMessage.error('获取文章列表失败')
  } finally {
    loading.value = false
  }
}

const handleCategoryChange = () => {
  pagination.current = 1
  loadList()
}

const goDetail = (id) => {
  router.push(`/user/knowledge/${id}`)
}

onMounted(() => {
  loadList()
})
</script>

<style scoped lang="scss">
.knowledge-container {
  padding: 20px;

  .filter-card {
    margin-bottom: 20px;

    .filter-bar {
      display: flex;
      align-items: center;

      .filter-label {
        font-weight: 600;
        margin-right: 12px;
        white-space: nowrap;
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
