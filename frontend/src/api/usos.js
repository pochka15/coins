import axios from 'axios'
import auth from '../security/auth'

/**
 * @typedef {{ course_id: string, match: string }} CourseByNameResponseItem
 */

/**
 * @typedef {{ items: CourseByNameResponseItem[], next_page: boolean }} CourseByNameResponse
 */

const usosClient = axios.create({ baseURL: 'https://apps.usos.pw.edu.pl' })

/**
 * Get course ids  by name
 * @param {string} name
 * @return {Promise<string[]>}
 */
export function getCourseIdsByName(name) {
  const params = new URLSearchParams()
  params.append('lang', 'pl')
  params.append('name', name)
  params.append('format', 'json')

  return usosClient.post('/services/courses/search', params).then(response => {
    /** @type {CourseByNameResponse} */
    const data = response.data
    return data.items.map(x => x.course_id)
  })
}

/**
 * Get course edition
 * @param {string} courseId
 * @param {string} semester
 * @return {Promise<ApiCourseEdition>}
 */
export function getCourseEdition(courseId, semester) {
  return auth
    .getClient()
    .get(`/usos/course_edition?courseId=${courseId}&semester=${semester}`)
    .then(r => r.data)
}
